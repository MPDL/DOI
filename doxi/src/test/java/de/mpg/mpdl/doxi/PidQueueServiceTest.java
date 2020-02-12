package de.mpg.mpdl.doxi;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.jersey.servlet.ServletContainer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mpg.mpdl.doxi.doi.rest.JerseyApplicationConfig;
import de.mpg.mpdl.doxi.pidcache.PidQueue;
import de.mpg.mpdl.doxi.pidcache.PidQueueService;
import de.mpg.mpdl.doxi.pidcache.model.Pid;
import de.mpg.mpdl.doxi.pidcache.model.PidID;
import de.mpg.mpdl.doxi.util.EMF;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PidQueueServiceTest {
  private static final Logger LOG = LoggerFactory.getLogger(PidQueueServiceTest.class);

  private HttpServer server; // Lightweight Grizzly container that runs JAX-RS applications,
                             // embedded in the application
  private EntityManager em;
  private PidQueueService pidQueueService;

  @Before
  public void setUp() throws Exception {
    // Server
    this.server = new HttpServer();

    NetworkListener listener = new NetworkListener("grizzly2", "localhost", 8123);
    this.server.addListener(listener);

    WebappContext ctx = new WebappContext("ctx", "/");
    ctx.addServlet("de.mpg.mpdl.doi.rest.JerseyApplicationConfig",
        new ServletContainer(new JerseyApplicationConfig(null))).addMapping("/rest/*");
    ctx.addListener("de.mpg.mpdl.doxi.rest.EMF");
    ctx.deploy(this.server);

    this.server.start();

    int i = 0;
    while (this.server.isStarted() == false && i < 10) {
      Thread.sleep(1000);
      i++;
    }
    
    // Sonstige
    this.em = EMF.emf.createEntityManager();
    this.pidQueueService = new PidQueueService(em);
  }

  @After
  public void tearDown() throws Exception {
    this.em.close();
    this.server.shutdown();

    int i = 0;
    while (this.server.isStarted() == true && i < 10) {
      Thread.sleep(1000);
      i++;
    }
  }

  @Ignore
  @Test
  public void test_1_removeAll_empty() throws Exception {
    LOG.info("--------------------- STARTING test_1_removeAll_empty ---------------------");

    long size = this.pidQueueService.getSize();

    while (size > 0) {
      List<Pid> list = this.pidQueueService.getFirstBlock(1);
      for (Pid pid : list) {
        this.em.getTransaction().begin();
        this.pidQueueService.remove(pid.getPidID());
        this.em.getTransaction().commit();
      }
      size = this.pidQueueService.getSize();
    }

    Assert.assertEquals(0, size);

    boolean empty = this.pidQueueService.isEmpty();

    Assert.assertTrue(empty);

    LOG.info("--------------------- FINISHED test_1_removeAll_empty ---------------------");
  }

  @Ignore
  @Test
  public void test_2_add_getFirstBlock() throws Exception {
    LOG.info("--------------------- STARTING test_2_add_getFirstBlock ---------------------");

    List<Pid> list = new ArrayList<Pid>();

    Pid pid1 = new Pid(PidID.create("TEST1/00-001Z-0000-002B-FC67-5"), URI.create("http://1"));
    list.add(pid1);
    this.em.getTransaction().begin();
    this.pidQueueService.add(pid1);
    this.em.getTransaction().commit();

    Pid pid2 = new Pid(PidID.create("TEST2/00-001Z-0000-002B-FC67-5"), URI.create("http://2"));
    list.add(pid2);
    this.em.getTransaction().begin();
    this.pidQueueService.add(pid2);
    this.em.getTransaction().commit();

    Pid pid3 = new Pid(PidID.create("TEST3/00-001Z-0000-002B-FC67-5"), URI.create("http://3"));
    this.em.getTransaction().begin();
    this.pidQueueService.add(pid3);
    this.em.getTransaction().commit();

    List<Pid> _list = this.pidQueueService.getFirstBlock(2);

    for (Pid pid : _list) {
      Assert.assertEquals(pid, list.get(0));
      list.remove(0);
    }

    LOG.info("--------------------- FINISHED test_2_add_getFirstBlock ---------------------");
  }

  @Ignore
  @Test
  public void test_3_retrieve() throws Exception {
    LOG.info("--------------------- STARTING test_3_retrieve ---------------------");

    PidID pidID = PidID.create("TEST1/00-001Z-0000-002B-FC67-5");
    PidQueue pidQueue = this.pidQueueService.retrieve(pidID);

    Assert.assertEquals(pidID, pidQueue.getID());

    LOG.info("--------------------- FINISHED test_3_retrieve ---------------------");
  }

  @Ignore
  @Test
  public void test_4_retrieve_notFound() throws Exception {
    LOG.info("--------------------- STARTING test_4_retrieve_notFound ---------------------");

    PidID pidID = PidID.create("TESTXX/00-001Z-0000-002B-FC67-5");
    PidQueue pidQueue = this.pidQueueService.retrieve(pidID);

    Assert.assertEquals(null, pidQueue);

    LOG.info("--------------------- FINISHED test_4_retrieve_notFound ---------------------");
  }

  @Ignore
  @Test
  public void test_5_search() throws Exception {
    LOG.info("--------------------- STARTING test_5_search ---------------------");

    URI url = URI.create("http://4");

    List<String> list = this.pidQueueService.search(url);

    Assert.assertTrue(list.isEmpty());

    Pid pid1 = new Pid(PidID.create("TEST4/00-001Z-0000-002B-FC67-5"), url);
    this.em.getTransaction().begin();
    this.pidQueueService.add(pid1);
    this.em.getTransaction().commit();

    list = this.pidQueueService.search(url);

    Assert.assertEquals(pid1.getUrl().toString(), list.get(0));

    LOG.info("--------------------- FINISHED test_5_search ---------------------");
  }

  @Ignore
  @Test
  public void test_6_update() throws Exception {
    LOG.info("--------------------- STARTING test_6_update ---------------------");

    Pid pid1 = new Pid(PidID.create("TESTU/00-001Z-0000-002B-FC67-5"), URI.create("http://5"));
    this.em.getTransaction().begin();
    this.pidQueueService.add(pid1);
    this.em.getTransaction().commit();

    Pid pid2 =
        new Pid(PidID.create("TESTU/00-001Z-0000-002B-FC67-5"), URI.create("http://5UPDATE"));

    PidQueue pidQueue = this.pidQueueService.retrieve(pid2.getPidID());
    if (pidQueue != null) {
      this.em.getTransaction().begin();
      pidQueue.setUrl(pid2.getUrl());
      this.em.getTransaction().commit();
    }

    Assert.assertEquals(pid1.getPidID(), pidQueue.getID());
    Assert.assertEquals(pid2.getUrl(), pidQueue.getUrl());

    LOG.info("--------------------- FINISHED test_6_update ---------------------");
  }

  @Ignore
  @Test
  public void test_7_removeAll_empty() throws Exception {
    LOG.info("--------------------- STARTING test_7_removeAll_empty ---------------------");

    long size = this.pidQueueService.getSize();

    while (size > 0) {
      List<Pid> _list = this.pidQueueService.getFirstBlock(1);
      for (Pid pid : _list) {
        this.em.getTransaction().begin();
        this.pidQueueService.remove(pid.getPidID());
        this.em.getTransaction().commit();
        size = this.pidQueueService.getSize();
      }
    }

    Assert.assertEquals(0, size);

    boolean empty = this.pidQueueService.isEmpty();

    Assert.assertTrue(empty);

    LOG.info("--------------------- FINISHED test_7_removeAll_empty ---------------------");
  }
}
