package de.mpg.mpdl.doxi.view;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.glassfish.jersey.server.mvc.MvcFeature;
import org.glassfish.jersey.server.mvc.mustache.MustacheMvcFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationPath("ui")
public class JerseyApplicationConfig extends ResourceConfig {

  private static final Logger LOG = LoggerFactory.getLogger(JerseyApplicationConfig.class);

	
  public JerseyApplicationConfig() {
	LOG.info("Initializing Jersey Application Configuration for UI application");

	packages(true, "de.mpg.mpdl.doxi.view","de.mpg.mpdl.doxi.security", "de.mpg.mpdl.doxi.rest.exceptionMapper");

    // Server Properties
    property(ServerProperties.RESPONSE_SET_STATUS_OVER_SEND_ERROR, "true");

    // Mustache
    property(MustacheMvcFeature.TEMPLATE_BASE_PATH, "/mustache");
    register(MustacheMvcFeature.class);
    register(MvcFeature.class);

  
    // Verwendung von Rollen -> @RolesAllowed
    register(RolesAllowedDynamicFeature.class);

  }
}
