package de.mpg.mpdl.doxi.rest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.ApplicationPath;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.glassfish.jersey.server.mvc.MvcFeature;
import org.glassfish.jersey.server.mvc.mustache.MustacheMvcFeature;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mpg.mpdl.doxi.controller.DataciteAPIController;
import de.mpg.mpdl.doxi.controller.DoiControllerInterface;
import de.mpg.mpdl.doxi.pidcache.GwdgClient;
import de.mpg.mpdl.doxi.pidcache.GwdgClientInterface;
import de.mpg.mpdl.doxi.security.DoxiRole;
import de.mpg.mpdl.doxi.security.DoxiUser;
import de.mpg.mpdl.doxi.util.PropertyReader;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;

@ApplicationPath("rest")
public class JerseyApplicationConfig extends ResourceConfig {
  private static final Logger LOG = LoggerFactory.getLogger(JerseyApplicationConfig.class);

  public static final EntityManagerFactory emf;

  static {
    // Pass JUL logs (Java Util Logging), used by Jersey, to Log4J
    // System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");

    Map<String, String> jpaDBProperties = new HashMap<String, String>();
    jpaDBProperties.put("javax.persistence.jdbc.driver", PropertyReader.getProperty(PropertyReader.DOXI_JDBC_DRIVER));
    jpaDBProperties.put("javax.persistence.jdbc.url", PropertyReader.getProperty(PropertyReader.DOXI_JDBC_URL));
    jpaDBProperties.put("javax.persistence.jdbc.user", PropertyReader.getProperty(PropertyReader.DOXI_JDBC_USER));
    jpaDBProperties.put("javax.persistence.jdbc.password", PropertyReader.getProperty(PropertyReader.DOXI_JDBC_PASSWORD));

    emf = Persistence.createEntityManagerFactory("default", jpaDBProperties);
  }

  public JerseyApplicationConfig() {
    // property("contextConfigLocation", "classpath:applicationContext.xml");
    
    // packages(true, "de.mpg.mpdl.doxi"); -> siehe web.xml

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
        bind(GwdgClient.class).to(GwdgClientInterface.class);
      }
    });

    // Logging
    registerInstances(new LoggingFilter(java.util.logging.Logger.getLogger("test"), true));
    
    // Verwendung von Rollen -> @RolesAllowed
    register(RolesAllowedDynamicFeature.class);
    
    // Swagger
    register(ApiListingResource.class);
    register(SwaggerSerializers.class);
    BeanConfig beanConfig = new BeanConfig();
    beanConfig.setVersion("1.0.0");
    // beanConfig.setSchemes(new String[]{"https", "http"});
    // beanConfig.setHost("localhost:8081");
    beanConfig.setBasePath("/doxi/rest");
    beanConfig.setResourcePackage("de.mpg.mpdl.doxi.rest");
    beanConfig.setScan(true);

    try {
      String createUser = PropertyReader.getProperty(PropertyReader.DOXI_ADMIN_CREATE);
      if ("true".equals(createUser)) {
        createAdminUser();
      }
    } catch (Exception e) {
      LOG.error("Error while creating admin user", e);
    }
  }

  private void createAdminUser() throws Exception {
    EntityManager em = emf.createEntityManager();

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
      LOG.info("Admin user " + username + " successfully created in database");
    } else {
      LOG.warn("Admin user was not created, because it already exists in database");
    }
  }

  public static void main(String[] args) throws Exception {

    HttpServer server = new HttpServer();
    NetworkListener listener = new NetworkListener("grizzly2", "localhost", 8081);
    server.addListener(listener);
    server.getServerConfiguration().addHttpHandler(new StaticHttpHandler("src/main/webapp"), "/doxi/");
    WebappContext ctx = new WebappContext("ctx", "/doxi");

    // If Java-config should be used, create a class SecurityWebApplicationInitializer extends
    // AbstractSecurityWebApplicationInitializer
    // and a config and use the following method:
    // SecurityWebApplicationInitializer initializer = new SecurityWebApplicationInitializer();
    // initializer.onStartup(ctx);

    // If XML-Config should be used use SpringWebApplicationInitializer from package jersey-spring
    // 3, which does the following:
    // ctx.addContextInitParameter("contextConfigLocation", "classpath:applicationContext.xml");
    // ctx.addListener(ContextLoaderListener.class);
    // ctx.addListener(RequestContextListener.class);

    // Register Jersey Servlet
    Set<Class<?>> set = new HashSet<Class<?>>();
    set.add(JerseyApplicationConfig.class);

    // new JerseyServletContainerInitializer().onStartup(set, ctx);
    ctx.addServlet("de.mpg.mpdl.doxi.rest.JerseyApplicationConfig",
        new ServletContainer(new JerseyApplicationConfig())).addMapping("/rest/*");

    ctx.deploy(server);

    server.start();

    Thread.currentThread().join();
  }
}
