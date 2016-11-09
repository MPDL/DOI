package de.mpg.mpdl.doxi;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.servlet.ServletContainer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mpg.mpdl.doxi.pidcache.PidServiceResponseVO;
import de.mpg.mpdl.doxi.pidcache.XMLTransforming;
import de.mpg.mpdl.doxi.rest.JerseyApplicationConfig;
import de.mpg.mpdl.doxi.rest.PidResource;
import de.mpg.mpdl.doxi.util.PropertyReader;

// ####################################################################################
// Voraussetzung für Test mit GWDG Pid Handler:
// - GWDG Pid Handler erreichbar
// - InitializerServlet läuft
// - doxi.pid.cache.empty.interval << SLEEP
//
// Die Tests laufen auch, wenn der GWDG Pid Handler nicht zur Verfügung steht
// ####################################################################################

public class PidResourceClientTest {
  private static final Logger LOG = LoggerFactory.getLogger(PidResourceClientTest.class);

  private static final long SLEEP = 5000L;

  private static final String URL = "url";
  private static final String ID = "id";

  private HttpServer server; // Lightweight Grizzly container that runs JAX-RS applications,
                             // embedded in the application
  private WebTarget target;
  private XMLTransforming xmlTransforming;

  @Before
  public void setUp() throws Exception {
    // Client
    ClientConfig clientConfig = new ClientConfig();

    String user = PropertyReader.getProperty(PropertyReader.DOXI_ADMIN_USER);
    String passwd = PropertyReader.getProperty(PropertyReader.DOXI_ADMIN_PASSWORD);
    HttpAuthenticationFeature feature =
        HttpAuthenticationFeature.basicBuilder().credentials(user, passwd).build();
    clientConfig.register(feature);

    this.target = ClientBuilder.newClient(clientConfig).target("http://localhost:8123/rest/pid");

    // Server
    this.server = new HttpServer();

    NetworkListener listener = new NetworkListener("grizzly2", "localhost", 8123);
    this.server.addListener(listener);

    WebappContext ctx = new WebappContext("ctx", "/");
    ctx.addServlet("de.mpg.mpdl.doi.rest.JerseyApplicationConfig",
        new ServletContainer(new JerseyApplicationConfig())).addMapping("/rest/*");
    ctx.addListener("de.mpg.mpdl.doxi.rest.EMF");
    ctx.deploy(this.server);

    this.server.start();

    // XMLTransforming
    this.xmlTransforming = new XMLTransforming();
  }

  @After
  public void tearDown() throws Exception {
    this.server.shutdown();
  }

  @Ignore
  @Test
  public void create() throws Exception {
    LOG.info("--------------------- STARTING create ---------------------");

    String url = "www.test.de/" + Math.random();
    LOG.info("Url {}", url);

    Form form = new Form();
    form.param(URL, url);

    Response response = target.path(PidResource.PATH_CREATE).request(MediaType.TEXT_PLAIN_TYPE)
        .post(Entity.form(form));
    Assert.assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

    waitForPidQueueIsEmpty();

    LOG.info("Url {}", url);
    response = target.path(PidResource.PATH_CREATE).request(MediaType.TEXT_PLAIN_TYPE)
        .post(Entity.form(form));
    Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

    LOG.info("--------------------- FINISHED create ---------------------");
  }

  @Ignore
  @Test
  public void retrieve() throws Exception {
    LOG.info("--------------------- STARTING retrieve ---------------------");

    String url = "www.test.de/" + Math.random();
    LOG.info("Url {}", url);

    Form form = new Form();
    form.param(URL, url);

    Response response = target.path(PidResource.PATH_CREATE).request(MediaType.TEXT_PLAIN_TYPE)
        .post(Entity.form(form));
    Assert.assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

    String xml = response.readEntity(String.class);
    PidServiceResponseVO vo = xmlTransforming.transformToVO(xml);
    String id = vo.getIdentifier();

    waitForPidQueueIsEmpty();

    response = target.path(PidResource.PATH_RETRIEVE).queryParam(ID, id)
        .request(MediaType.TEXT_PLAIN_TYPE).get();
    Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

    LOG.info("--------------------- FINISHED retrieve ---------------------");
  }

  @Ignore
  @Test
  public void search() throws Exception {
    LOG.info("--------------------- STARTING search ---------------------");

    String url = "www.test.de/" + Math.random();
    LOG.info("Url {}", url);

    Form form = new Form();
    form.param(URL, url);

    Response response = target.path(PidResource.PATH_SEARCH).queryParam(URL, url)
        .request(MediaType.TEXT_PLAIN_TYPE).get();
    Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

    response = target.path(PidResource.PATH_CREATE).request(MediaType.TEXT_PLAIN_TYPE)
        .post(Entity.form(form));
    Assert.assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

    waitForPidQueueIsEmpty();

    response = target.path(PidResource.PATH_SEARCH).queryParam(URL, url)
        .request(MediaType.TEXT_PLAIN_TYPE).get();
    Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

    LOG.info("--------------------- FINISHED search ---------------------");
  }

  @Ignore
  @Test
  public void update() throws Exception {
    LOG.info("--------------------- STARTING update ---------------------");

    String url = "www.test.de/" + Math.random();
    LOG.info("Url {}", url);

    Form form = new Form();
    form.param(URL, url);

    Response response = target.path(PidResource.PATH_CREATE).request(MediaType.TEXT_PLAIN_TYPE)
        .post(Entity.form(form));
    Assert.assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

    String xml = response.readEntity(String.class);
    PidServiceResponseVO vo = xmlTransforming.transformToVO(xml);
    String id = vo.getIdentifier();

    waitForPidQueueIsEmpty();

    String newUrl = url + "T";
    
    form = new Form();
    form.param(URL, newUrl);
    form.param(ID, id);

    response = target.path(PidResource.PATH_UPDATE).request(MediaType.TEXT_PLAIN_TYPE)
        .put(Entity.form(form));

    Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

    xml = response.readEntity(String.class);
    vo = this.xmlTransforming.transformToVO(xml);
    String _id = vo.getIdentifier();
    String _url = vo.getUrl();

    Assert.assertEquals(id, _id);
    Assert.assertEquals(newUrl, _url);

    LOG.info("--------------------- FINISHED update ---------------------");
  }

  @Ignore
  @Test
  public void getCacheSize() throws Exception {
    LOG.info("--------------------- STARTING getCacheSize ---------------------");

    Response response =
        target.path(PidResource.PATH_CACHE_SIZE).request(MediaType.TEXT_PLAIN_TYPE).get();
    Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

    LOG.info("--------------------- FINISHED getCacheSize ---------------------");
  }

  @Ignore
  @Test
  public void getQueueSize() throws Exception {
    LOG.info("--------------------- STARTING getQueueSize ---------------------");

    Response response =
        target.path(PidResource.PATH_QUEUE_SIZE).request(MediaType.TEXT_PLAIN_TYPE).get();
    Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

    LOG.info("--------------------- FINISHED getQueueSize ---------------------");
  }

  private void waitForPidQueueIsEmpty() throws InterruptedException {
    LOG.info("SLEEP Start");
    Thread.sleep(SLEEP);
    LOG.info("SLEEP Stop");
  }
}
