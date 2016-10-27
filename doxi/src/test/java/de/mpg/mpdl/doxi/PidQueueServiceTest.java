package de.mpg.mpdl.doxi;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mpg.mpdl.doxi.pidcache.Pid;
import de.mpg.mpdl.doxi.pidcache.PidID;
import de.mpg.mpdl.doxi.pidcache.PidQueueService;
import de.mpg.mpdl.doxi.rest.JerseyApplicationConfig;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PidQueueServiceTest {
  private static final Logger LOG = LoggerFactory.getLogger(PidQueueServiceTest.class);

  private EntityManager em;
  private PidQueueService pidQueueService;

  @Before
  public void setUp() throws Exception {
    this.em = JerseyApplicationConfig.emf.createEntityManager();
    this.pidQueueService = new PidQueueService(em);
  }

  @After
  public void tearDown() throws Exception {
    this.em.close();
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

    Assert.assertEquals(size, 0);

    boolean empty = this.pidQueueService.isEmpty();

    Assert.assertTrue(empty);

    LOG.info("--------------------- FINISHED test_1_removeAll_empty ---------------------");
  }

  @Ignore
  @Test
  public void test_2_add_getFirstBlock() throws Exception {
    LOG.info("--------------------- STARTING test_2_add_getFirstBlock ---------------------");

    List<Pid>list = new ArrayList<Pid>();
    
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
    
    List<Pid>_list = this.pidQueueService.getFirstBlock(2);
    
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
    Pid _pid = this.pidQueueService.retrieve(pidID);

    Assert.assertEquals(pidID, _pid.getPidID());

    LOG.info("--------------------- FINISHED test_3_retrieve ---------------------");
  }
  
  @Ignore
  @Test
  public void test_4_retrieve_notFound() throws Exception {
    LOG.info("--------------------- STARTING test_4_retrieve_notFound ---------------------");

    PidID pidID = PidID.create("TESTXX/00-001Z-0000-002B-FC67-5");
    Pid _pid = this.pidQueueService.retrieve(pidID);

    Assert.assertEquals(null, _pid);

    LOG.info("--------------------- FINISHED test_4_retrieve_notFound ---------------------");
  }
  
  @Ignore
  @Test
  public void test_5_search() throws Exception {
    LOG.info("--------------------- STARTING test_5_search ---------------------");

    URI url = URI.create("http://1");

    Pid _pid = this.pidQueueService.search(url);
    
    Assert.assertEquals(null, _pid);
    
    Pid pid1 = new Pid(PidID.create("TEST1/00-001Z-0000-002B-FC67-5"), url);
    this.em.getTransaction().begin();
    this.pidQueueService.add(pid1);
    this.em.getTransaction().commit();
    
    _pid = this.pidQueueService.search(url);

    Assert.assertEquals(pid1, _pid);

    LOG.info("--------------------- FINISHED test_5_search ---------------------");
  }
  
  @Ignore
  @Test
  public void test_6_update() throws Exception {
    LOG.info("--------------------- STARTING test_6_update ---------------------");

    Pid pid1 = new Pid(PidID.create("TESTU/00-001Z-0000-002B-FC67-5"), URI.create("http://1"));
    this.em.getTransaction().begin();
    this.pidQueueService.add(pid1);
    this.em.getTransaction().commit();
    
    Pid pid2 = new Pid(PidID.create("TESTU/00-001Z-0000-002B-FC67-5"), URI.create("http://1UPDATE"));
    this.em.getTransaction().begin();
    Pid _pid = this.pidQueueService.update(pid2);
    this.em.getTransaction().commit();
    
    Assert.assertEquals(pid1.getPidID(), _pid.getPidID());
    Assert.assertEquals(pid2.getUrl(), _pid.getUrl());

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

    Assert.assertEquals(size, 0);

    boolean empty = this.pidQueueService.isEmpty();

    Assert.assertTrue(empty);

    LOG.info("--------------------- FINISHED test_7_removeAll_empty ---------------------");
  }
}
