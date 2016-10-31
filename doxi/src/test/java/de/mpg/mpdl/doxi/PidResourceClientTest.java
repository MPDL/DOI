package de.mpg.mpdl.doxi;

import javax.persistence.EntityManager;
import javax.ws.rs.client.ClientBuilder;
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

import de.mpg.mpdl.doxi.rest.EMF;
import de.mpg.mpdl.doxi.rest.JerseyApplicationConfig;
import de.mpg.mpdl.doxi.rest.PidResource;
import de.mpg.mpdl.doxi.util.PropertyReader;

public class PidResourceClientTest {
  private static final Logger LOG = LoggerFactory.getLogger(PidResourceClientTest.class);

  private EntityManager em;
  private HttpServer server; // Lightweight Grizzly container that runs JAX-RS applications, embedded in the application
  private WebTarget target;
  
  @Before
  public void setUp() throws Exception {
    this.em = EMF.emf.createEntityManager();
    
    // Client
    ClientConfig clientConfig = new ClientConfig();
    clientConfig.register(new CsrfProtectionFilter("doxi test"));  // filter with X-Requested-By header

    HttpAuthenticationFeature feature = HttpAuthenticationFeature.basicBuilder().credentials(
        PropertyReader.getProperty(PropertyReader.DOXI_ADMIN_USER),
        PropertyReader.getProperty(PropertyReader.DOXI_ADMIN_PASSWORD))
        .build();
    clientConfig.register(feature);

    this.target = ClientBuilder.newClient(clientConfig).target("http://localhost:8123/rest/pid");
    
    // Server
    this.server = new HttpServer();
    NetworkListener listener = new NetworkListener("grizzly2", "localhost", 8123);
    this.server.addListener(listener);

    WebappContext ctx = new WebappContext("ctx", "/");
    ctx.addServlet("de.mpg.mpdl.doi.rest.JerseyApplicationConfig",
        new ServletContainer(new JerseyApplicationConfig())).addMapping("/rest/*");
    ctx.deploy(this.server);

    this.server.start();
    
  }

  @After
  public void tearDown() throws Exception {
    this.em.close();
    this.server.shutdown();
  }

  @Ignore
  @Test
  public void create() throws Exception { // TODO TEST
    LOG.info("--------------------- STARTING create ---------------------");

    LOG.info("--------------------- FINISHED create ---------------------");
  }

  @Ignore
  @Test
  public void retrieve() throws Exception { // TODO TEST
    LOG.info("--------------------- STARTING retrieve ---------------------");

    LOG.info("--------------------- FINISHED retrieve ---------------------");
  }
  
  @Ignore
  @Test
  public void search() throws Exception { // TODO TEST
    LOG.info("--------------------- STARTING search ---------------------");

    LOG.info("--------------------- FINISHED search ---------------------");
  }

  @Ignore
  @Test
  public void update() throws Exception { // TODO TEST
    LOG.info("--------------------- STARTING update ---------------------");

    LOG.info("--------------------- FINISHED update ---------------------");
  }

  @Ignore
  @Test
  public void getCacheSize() throws Exception {
    LOG.info("--------------------- STARTING getCacheSize ---------------------");

    Response result = target.path(PidResource.PATH_CACHE_SIZE).request(MediaType.TEXT_PLAIN_TYPE).get();
    Assert.assertEquals(Response.Status.OK.getStatusCode(), result.getStatus());
    
    LOG.info("--------------------- FINISHED getCacheSize ---------------------");
  }

  @Ignore
  @Test
  public void getQueueSize() throws Exception {
    LOG.info("--------------------- STARTING getQueueSize ---------------------");

    Response result = target.path(PidResource.PATH_QUEUE_SIZE).request(MediaType.TEXT_PLAIN_TYPE).get();
    Assert.assertEquals(Response.Status.OK.getStatusCode(), result.getStatus());
    
    LOG.info("--------------------- FINISHED getQueueSize ---------------------");
  }
}