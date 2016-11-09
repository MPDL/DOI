package de.mpg.mpdl.doxi;

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

import de.mpg.mpdl.doxi.pidcache.PidCacheService;
import de.mpg.mpdl.doxi.pidcache.PidID;
import de.mpg.mpdl.doxi.rest.EMF;
import de.mpg.mpdl.doxi.rest.JerseyApplicationConfig;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PidCacheServiceTest {
  private static final Logger LOG = LoggerFactory.getLogger(PidCacheServiceTest.class);

  private HttpServer server; // Lightweight Grizzly container that runs JAX-RS applications,
                             // embedded in the application
  private EntityManager em;
  private PidCacheService pidCacheService;

  @Before
  public void setUp() throws Exception {
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

    int i = 0;
    while (this.server.isStarted() == false && i < 10) {
      Thread.sleep(1000);
      i++;
    }
    
    // Sonstige
    this.em = EMF.emf.createEntityManager();
    this.pidCacheService = new PidCacheService(this.em);
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

    long size = this.pidCacheService.getSize();

    while (size > 0) {
      PidID pidID = this.pidCacheService.getFirst();
      if (pidID != null) {
        this.em.getTransaction().begin();
        this.pidCacheService.remove(pidID);
        this.em.getTransaction().commit();
        size = this.pidCacheService.getSize();
      }
    }

    Assert.assertEquals(0, size);

    boolean empty = this.pidCacheService.isEmpty();

    Assert.assertTrue(empty);

    LOG.info("--------------------- FINISHED test_1_removeAll_empty ---------------------");
  }

  @Ignore
  @Test
  public void test_2_add_getFirst() throws Exception {
    LOG.info("--------------------- STARTING test_2_add_getFirst ---------------------");

    PidID pidID1 = PidID.create("TEST1/00-001Z-0000-002B-FC67-5");
    this.em.getTransaction().begin();
    this.pidCacheService.add(pidID1);
    this.em.getTransaction().commit();
    PidID _pidID1 = this.pidCacheService.getFirst();

    Assert.assertEquals(pidID1, _pidID1);

    PidID pidID2 = PidID.create("TEST2/00-001Z-0000-002B-FC67-5");
    this.em.getTransaction().begin();
    this.pidCacheService.add(pidID2);
    this.em.getTransaction().commit();
    PidID _pidID2 = this.pidCacheService.getFirst();

    Assert.assertEquals(pidID1, _pidID2);

    PidID pidID3 = PidID.create("TEST3/00-001Z-0000-002B-FC67-5");
    this.em.getTransaction().begin();
    this.pidCacheService.add(pidID3);
    this.em.getTransaction().commit();
    PidID _pidID3 = this.pidCacheService.getFirst();

    Assert.assertEquals(pidID1, _pidID3);

    LOG.info("--------------------- FINISHED test_2_add_getFirst ---------------------");
  }

  @Ignore
  @Test
  public void test_3_remove() throws Exception {
    LOG.info("--------------------- STARTING test_4_remove ---------------------");

    long size1 = this.pidCacheService.getSize();

    PidID pidID1 = PidID.create("TEST1/00-001Z-0000-002B-FC67-5");
    this.em.getTransaction().begin();
    this.pidCacheService.remove(pidID1);
    this.em.getTransaction().commit();

    long size2 = this.pidCacheService.getSize();

    Assert.assertEquals(size1, size2 + 1);

    PidID pidID2 = PidID.create("TEST2/00-001Z-0000-002B-FC67-5");
    this.em.getTransaction().begin();
    this.pidCacheService.remove(pidID2);
    this.em.getTransaction().commit();

    long size3 = this.pidCacheService.getSize();

    Assert.assertEquals(size2, size3 + 1);

    PidID pidID3 = PidID.create("TEST3/00-001Z-0000-002B-FC67-5");
    this.em.getTransaction().begin();
    this.pidCacheService.remove(pidID3);
    this.em.getTransaction().commit();

    long size4 = this.pidCacheService.getSize();

    Assert.assertEquals(size3, size4 + 1);

    LOG.info("--------------------- FINISHED test_3_remove ---------------------");
  }

  @Ignore
  @Test
  public void test_4_full() throws Exception {
    LOG.info("--------------------- STARTING test_4_full ---------------------");

    long size = this.pidCacheService.getSize();

    int i = 0;
    while (size < this.pidCacheService.getSizeMax()) {
      PidID pidID = PidID.create("TEST/" + ++i);
      this.em.getTransaction().begin();
      this.pidCacheService.add(pidID);
      this.em.getTransaction().commit();
      size = this.pidCacheService.getSize();
    }

    boolean full = this.pidCacheService.isFull();

    Assert.assertTrue(full);

    LOG.info("--------------------- FINISHED test_4_full ---------------------");
  }

  @Ignore
  @Test
  public void test_5_removeAll_empty() throws Exception {
    LOG.info("--------------------- STARTING test_5_removeAll_empty ---------------------");

    long size = this.pidCacheService.getSize();

    while (size > 0) {
      PidID pidID = this.pidCacheService.getFirst();
      if (pidID != null) {
        this.em.getTransaction().begin();
        this.pidCacheService.remove(pidID);
        this.em.getTransaction().commit();
        size = this.pidCacheService.getSize();
      }
    }

    Assert.assertEquals(0, size);

    boolean empty = this.pidCacheService.isEmpty();

    Assert.assertTrue(empty);

    LOG.info("--------------------- FINISHED test_5_removeAll_empty ---------------------");
  }
}
