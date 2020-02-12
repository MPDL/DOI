package de.mpg.mpdl.doxi.pidcache.rest;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Context;

import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mpg.mpdl.doxi.pidcache.PidService;
import de.mpg.mpdl.doxi.pidcache.PidServiceInterface;
import io.swagger.v3.jaxrs2.integration.JaxrsOpenApiContextBuilder;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.SwaggerConfiguration;

@ApplicationPath("pid")
public class JerseyApplicationConfigPid extends ResourceConfig {
  private static final Logger LOG = LoggerFactory.getLogger(JerseyApplicationConfigPid.class);

  
  public JerseyApplicationConfigPid(@Context ServletConfig servletConfig) {
	  LOG.info("Initializing Jersey Application Configuration for pid application");


	packages(true, "de.mpg.mpdl.doxi.pidcache", "de.mpg.mpdl.doxi.security", "de.mpg.mpdl.doxi.rest.exceptionMapper");

    //As there are two Jersey Applications (doi and pid), use these init params for swagger to distinguish
   /*
    property("swagger.scanner.id", "doi"); 
    property("swagger.config.id", "doi"); 
    property("swagger.context.id", "doi");
*/
    // Server Properties
    property(ServerProperties.RESPONSE_SET_STATUS_OVER_SEND_ERROR, "true");
    // Interfaces
    register(new AbstractBinder() {
      @Override
      protected void configure() {
        bind(PidService.class).to(PidServiceInterface.class);
      }
    });

    // Logging
    registerInstances(new LoggingFeature(java.util.logging.Logger.getLogger(this.getClass().getCanonicalName())));

    // Verwendung von Rollen -> @RolesAllowed
    register(RolesAllowedDynamicFeature.class);

    // Swagger
 
    SwaggerConfiguration oasConfig = new SwaggerConfiguration()
            .prettyPrint(true)
            .readAllResources(false)
            .resourcePackages(Stream.of("de.mpg.mpdl.doxi.pidcache.rest").collect(Collectors.toSet()));
    
    OpenApiResource openApiResource = new OpenApiResource();
    openApiResource.openApiConfiguration(oasConfig);
    register(openApiResource);


    
  }

}
