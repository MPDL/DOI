package de.mpg.mpdl.doxi.doi.rest;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Context;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mpg.mpdl.doxi.doi.controller.DataciteAPIController;
import de.mpg.mpdl.doxi.doi.controller.DoiControllerInterface;
import de.mpg.mpdl.doxi.security.DoxiRole;
import de.mpg.mpdl.doxi.security.DoxiUser;
import de.mpg.mpdl.doxi.util.EMF;
import de.mpg.mpdl.doxi.util.PropertyReader;
import io.swagger.v3.jaxrs2.integration.JaxrsOpenApiContextBuilder;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.SwaggerConfiguration;

@ApplicationPath("doi")
public class JerseyApplicationConfig extends ResourceConfig {

	private static final Logger LOG = LoggerFactory.getLogger(JerseyApplicationConfig.class);

	public JerseyApplicationConfig(@Context ServletConfig servletConfig) {
		LOG.info("Initializing Jersey Application Configuration for doi application");

		java.util.logging.Logger julLogger = java.util.logging.Logger.getLogger(this.getClass().getCanonicalName());
		register(new LoggingFeature(julLogger));

		packages(true, "de.mpg.mpdl.doxi.doi", "de.mpg.mpdl.doxi.security", "de.mpg.mpdl.doxi.rest.exceptionMapper");

		// Server Properties
		property(ServerProperties.RESPONSE_SET_STATUS_OVER_SEND_ERROR, "true");

		// Interfaces
		register(new AbstractBinder() {
			@Override
			protected void configure() {
				bind(DataciteAPIController.class).to(DoiControllerInterface.class);
			}
		});

		// Verwendung von Rollen -> @RolesAllowed
		register(RolesAllowedDynamicFeature.class);

		// Swagger
		SwaggerConfiguration oasConfig = new SwaggerConfiguration().prettyPrint(true)
				.readAllResources(true)
				.resourcePackages(Stream.of("de.mpg.mpdl.doxi.doi.rest").collect(Collectors.toSet()));
		
	    OpenApiResource openApiResource = new OpenApiResource();
	    openApiResource.openApiConfiguration(oasConfig);
	    register(openApiResource);


		try {
			String createUser = PropertyReader.getProperty(PropertyReader.DOXI_ADMIN_CREATE);
			if ("true".equals(createUser)) {
				createAdminUser();
			}
		} catch (Exception e) {
			LOG.error("Error while creating admin user\n{}", e);
		}
	}

	private void createAdminUser() throws Exception {
		EntityManager em = EMF.emf.createEntityManager();

		try {
			String username = PropertyReader.getProperty(PropertyReader.DOXI_ADMIN_USER);
			DoxiUser alreadyExistsUser = em.find(DoxiUser.class, username);

			if (alreadyExistsUser == null) {
				DoxiUser adminUser = new DoxiUser();
				adminUser.setUsername(username);
				adminUser.setPassword(PropertyReader.getProperty(PropertyReader.DOXI_ADMIN_PASSWORD));
				adminUser.setPrefix(PropertyReader.getProperty(PropertyReader.DOXI_ADMIN_PREFIX));
				DoxiRole role = new DoxiRole("admin", PropertyReader.getProperty(PropertyReader.DOXI_ADMIN_USER));
				adminUser.getRoles().add(role);
				em.getTransaction().begin();
				em.persist(adminUser);
				em.getTransaction().commit();
				LOG.info("Admin user {} successfully created in database", username);
			} else {
				LOG.warn("Admin user was not created, because it already exists in database");
			}
		} catch (Exception e) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}
}
