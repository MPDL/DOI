package de.mpg.mpdl.doxi.rest;

import javax.persistence.EntityManager;
import javax.ws.rs.ApplicationPath;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.glassfish.jersey.server.mvc.MvcFeature;
import org.glassfish.jersey.server.mvc.mustache.MustacheMvcFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mpg.mpdl.doxi.controller.DataciteAPIController;
import de.mpg.mpdl.doxi.controller.DoiControllerInterface;
import de.mpg.mpdl.doxi.pidcache.PidService;
import de.mpg.mpdl.doxi.pidcache.PidServiceInterface;
import de.mpg.mpdl.doxi.security.DoxiRole;
import de.mpg.mpdl.doxi.security.DoxiUser;
import de.mpg.mpdl.doxi.util.EMF;
import de.mpg.mpdl.doxi.util.PropertyReader;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;

@ApplicationPath("rest")
public class JerseyApplicationConfig extends ResourceConfig {
  private static final Logger LOG = LoggerFactory.getLogger(JerseyApplicationConfig.class);

  public JerseyApplicationConfig() {
    packages(true, "de.mpg.mpdl.doxi");

    // Server Properties
    property(ServerProperties.RESPONSE_SET_STATUS_OVER_SEND_ERROR, "true");

    // Mustache
    property(MustacheMvcFeature.TEMPLATE_BASE_PATH, "/mustache");
    register(MustacheMvcFeature.class);
    register(MvcFeature.class);

    // Interfaces
    register(new AbstractBinder() {
      @Override
      protected void configure() {
        bind(DataciteAPIController.class).to(DoiControllerInterface.class);
        bind(PidService.class).to(PidServiceInterface.class);
      }
    });

    // Logging
    registerInstances(new LoggingFilter(java.util.logging.Logger.getLogger("DoxiJersey"), true));

    // Verwendung von Rollen -> @RolesAllowed
    register(RolesAllowedDynamicFeature.class);

    // Swagger
    register(ApiListingResource.class);
    register(SwaggerSerializers.class);
    BeanConfig beanConfig = new BeanConfig();
    beanConfig.setVersion("1.0.0");
    beanConfig.setBasePath("/doxi/rest");
    beanConfig.setResourcePackage("de.mpg.mpdl.doxi.rest");
    beanConfig.setScan(true);

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
        DoxiRole role =
            new DoxiRole("admin", PropertyReader.getProperty(PropertyReader.DOXI_ADMIN_USER));
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
