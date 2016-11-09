package de.mpg.mpdl.doxi;

import java.io.InputStream;
import java.util.Scanner;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.client.filter.CsrfProtectionFilter;
import org.glassfish.jersey.servlet.ServletContainer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mpg.mpdl.doxi.controller.DataciteAPIController;
import de.mpg.mpdl.doxi.rest.JerseyApplicationConfig;
import de.mpg.mpdl.doxi.util.PropertyReader;

public class SimpleTest {

  private static final Logger logger = LoggerFactory.getLogger(SimpleTest.class);

  private String testDoi = "10.15771/pure.999223";
  private String url = "http://dev-pubman.mpdl.mpg.de/pubman/item/escidoc:844691";

  private InputStream updatedMetadata =
      SimpleTest.class.getResourceAsStream("/doi_update_metadata.xml");

  private WebTarget target;
  private HttpServer server;

  /*
   * @Override protected Application configure() {
   * 
   * 
   * return new JerseyApplicationConfig();
   * 
   * 
   * //return new ResourceConfig(DOIResource.class, DoxiExceptionMapper.class,
   * DoiAlreadyExistsMapper.class, ExceptionMapper.class, SecurityConfig.class,
   * SecurityWebApplicationInitializer.class); }
   */
  /*
   * @Override protected DeploymentContext configureDeployment(){ ServletDeploymentContext context =
   * ServletDeploymentContext .builder(new JerseyApplicationConfig())
   * //.addFilter(DelegatingFilterProxy.class, "springSecurityFilterChain")
   * 
   * //.contextParam("contextConfigLocation", "classpath:applicationContext.xml") .build();
   * 
   * return context;
   * 
   * }
   * 
   */
  @Before
  public void setUp() throws Exception {

    // HttpServer server =
    // GrizzlyHttpServerFactory.createHttpServer(URI.create("http://localhost:8123"), new
    // JerseyApplicationConfig());
    /*
     * TestContainer test = new
     * JettyTestContainerFactory().create(URI.create("http://localhost:8123/"),
     * DeploymentContext.builder(new JerseyApplicationConfig()).build()); //
     * createServer(URI.create("http://localhost:8123")); test.start();
     */

    server = new HttpServer();
    NetworkListener listener = new NetworkListener("grizzly2", "localhost", 8123);
    server.addListener(listener);

    WebappContext ctx = new WebappContext("ctx", "/");

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
    ctx.addServlet("de.mpg.mpdl.doi.rest.JerseyApplicationConfig",
        new ServletContainer(new JerseyApplicationConfig())).addMapping("/rest/*");
    ctx.addListener("de.mpg.mpdl.doxi.rest.EMF");

    ctx.deploy(server);

    server.start();

    ClientConfig clientConfig = new ClientConfig();
    clientConfig.register(new CsrfProtectionFilter("doxi test"));

    HttpAuthenticationFeature feature = HttpAuthenticationFeature.basicBuilder().credentials(
        PropertyReader.getProperty(PropertyReader.DOXI_ADMIN_USER),
        PropertyReader.getProperty(PropertyReader.DOXI_ADMIN_PASSWORD))
        .build();
    clientConfig.register(feature);

    this.target = ClientBuilder.newClient(clientConfig).target("http://localhost:8123/rest/doi");
  }

  @After
  public void tearDown() throws Exception {
    server.shutdown();
    // server.stop();
  }

  @Ignore
  @Test
  public void testGetDoiList() throws Exception {

    logger.info("--------------------- STARTING testGetDoiList ---------------------");
    Response result = target.request().get();
    logger.info("Status: " + result.getStatus() + " expected 200");
    logger.info("List: " + result.readEntity(String.class));
    Assert.assertEquals(200, result.getStatus());

    // new DataciteAPIController().updateUrl(testDoi, url);
    logger.info("--------------------- FINISHED testGetDoiList ---------------------");
  }

  @Ignore
  @Test
  public void testCreateDoi() throws Exception {
    logger.info("--------------------- STARTING testCreateDoi ---------------------");


    String metadata = streamToString(SimpleTest.class.getResourceAsStream("/doi_metadata_v3.xml"));

    Response result = target.path(testDoi).queryParam("url", url).request(MediaType.TEXT_PLAIN_TYPE)
        .put(Entity.xml(metadata));
    logger.info("Status: " + result.getStatus() + " expected 201");
    logger.info("Message: " + result.readEntity(String.class));
    Assert.assertEquals(201, result.getStatus());

    logger.info("--------------------- FINISHED testCreateDoi ---------------------");
  }

  @Ignore
  @Test
  public void testCreateDoiV4() throws Exception {
    logger.info("--------------------- STARTING testCreateDoi ---------------------");


    String metadata = streamToString(SimpleTest.class.getResourceAsStream("/doi_metadata_v4.xml"));

    Response result = target.path(testDoi).queryParam("url", url).request(MediaType.TEXT_PLAIN_TYPE)
        .put(Entity.xml(metadata));
    logger.info("Status: " + result.getStatus() + " expected 201");
    logger.info("Message: " + result.readEntity(String.class));
    Assert.assertEquals(201, result.getStatus());

    logger.info("--------------------- FINISHED testCreateDoi ---------------------");
  }

  @Ignore
  @Test
  public void testCreateDoiAutoGenerated() throws Exception {
    logger.info("--------------------- STARTING testCreateDoiAutoGenerated ---------------------");


    String metadata = streamToString(SimpleTest.class.getResourceAsStream("/doi_metadata_v3.xml"));

    Response result =
        target.queryParam("url", url).request(MediaType.TEXT_PLAIN_TYPE).put(Entity.xml(metadata));
    logger.info("Status: " + result.getStatus() + " expected 201");
    logger.info("Message: " + result.readEntity(String.class));
    Assert.assertEquals(201, result.getStatus());

    logger.info("--------------------- FINISHED testCreateDoiAutoGenerated ---------------------");
  }

  @Ignore
  @Test
  public void testUpdateMd() throws Exception {
    logger.info("--------------------- STARTING testUpdateMd ---------------------");
    Response result =
        target.path(testDoi).queryParam("url", url).request().post(Entity.xml(updatedMetadata));
    logger.info("Status: " + result.getStatus() + " expected 200\nEntity: "
        + result.readEntity(String.class));
    Assert.assertEquals(201, result.getStatus());

    logger.info("--------------------- FINISHED testUpdateMd ---------------------");
  }

  @Ignore
  @Test
  public void testGetDoi() throws Exception {
    logger.info("--------------------- STARTING testGetDoi ---------------------");
    Response result = target.path(testDoi).queryParam("url", url).request().get();
    logger.info("Status: " + result.getStatus() + " expected 200");
    logger.info("Message: " + result.readEntity(String.class));
    Assert.assertEquals(200, result.getStatus());

    logger.info("--------------------- FINISHED testGetDoi ---------------------");
  }

  @Ignore
  @Test
  public void testException() {
    Response result = target.path("test").request().get();
    logger.info("Exception " + result.readEntity(String.class) + result.getStatus());

    Response result2 = target.path("test2").request().get();
    logger.info("Exception " + result2.readEntity(String.class) + result2.getStatus());
  }

  private static String streamToString(InputStream is) {
    String inputStreamString = new Scanner(is, "UTF-8").useDelimiter("\\A").next();
    return inputStreamString;
  }

  public static void main(String[] arg) {

    for (int i = 0; i < 1; i++) {

      new Thread() {
        public void run() {
          // System.out.println("thread");
          try {
            // logger.info("Next: " );
            DataciteAPIController contr = new DataciteAPIController();
            String suff = contr.getNextDoiSuffix();
            System.out.println(suff);

          } catch (Exception e) {
            System.out.println(e);
            // logger.info("Problem", e);
          }
        }
      }.start();
    }
  }
}
