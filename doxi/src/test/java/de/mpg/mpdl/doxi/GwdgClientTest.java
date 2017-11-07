package de.mpg.mpdl.doxi;

import java.net.URI;
import java.util.Date;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mpg.mpdl.doxi.pidcache.GwdgClient;
import de.mpg.mpdl.doxi.pidcache.model.Pid;
import de.mpg.mpdl.doxi.util.PropertyReader;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GwdgClientTest {
  private static final Logger LOG = LoggerFactory.getLogger(GwdgClientTest.class);

  private GwdgClient gwdgClient;
  private String dummyUrl;
  
  @Before
  public void setUp() throws Exception {
    this.gwdgClient = new GwdgClient();
    this.dummyUrl = PropertyReader.getProperty(PropertyReader.DOXI_PID_CACHE_DUMMY_URL);
  }

  @After
  public void tearDown() throws Exception {
  }

  @Ignore
  @Test
  public void test_1_ServiceAvailable() throws Exception {
    LOG.info("--------------------- STARTING test_1_ServiceAvailable ---------------------");

    boolean available = gwdgClient.serviceAvailable();

    Assert.assertTrue(available);

    LOG.info("--------------------- FINISHED test_1_ServiceAvailable ---------------------");
  }

  @Ignore
  @Test
  public void test_2_CreatePid() throws Exception {
    LOG.info("--------------------- STARTING test_2_CreatePid ---------------------");

    Pid pid =
        gwdgClient.create(URI.create(this.dummyUrl.concat(Long.toString(new Date().getTime()))));

    Assert.assertNotNull(pid);

    LOG.info("--------------------- FINISHED test_2_CreatePid ---------------------");
  }

  @Ignore
  @Test
  public void test_3_RetrievePid() throws Exception {
    LOG.info("--------------------- STARTING test_3_RetrievePid ---------------------");

    Pid pid =
        gwdgClient.create(URI.create(this.dummyUrl.concat(Long.toString(new Date().getTime()))));
    Assert.assertNotNull(pid);
    
    pid = gwdgClient.retrieve(pid.getPidID());

    Assert.assertNotNull(pid);

    LOG.info("--------------------- FINISHED test_3_RetrievePid ---------------------");
  }

  @Ignore
  @Test
  public void test_4_SearchPid() throws Exception {
    LOG.info("--------------------- STARTING test_4_SearchPid ---------------------");

    Pid pid =
        gwdgClient.create(URI.create(this.dummyUrl.concat(Long.toString(new Date().getTime()))));
    Assert.assertNotNull(pid);
    
    pid = gwdgClient.search(pid.getUrl());

    Assert.assertNotNull(pid);

    LOG.info("--------------------- FINISHED test_4_SearchPid ---------------------");
  }

  @Ignore
  @Test
  public void test_5_UpdatePid() throws Exception {
    LOG.info("--------------------- STARTING test_5_UpdatePid ---------------------");

    Pid pid =
        gwdgClient.create(URI.create(this.dummyUrl.concat(Long.toString(new Date().getTime()))));
    Assert.assertNotNull(pid);
    
    Pid uPid = new Pid(pid.getPidID(), URI.create(pid.getUrl().toString()+"TEST"));
    Pid pid_1 = gwdgClient.update(uPid);
    Pid pid_2 = gwdgClient.update(uPid); // mit gleicher URL nochmal

    Assert.assertNotNull(pid_1);
    Assert.assertEquals(pid.getPidID(), pid_1.getPidID());
    Assert.assertEquals(uPid.getUrl(), pid_1.getUrl());
    Assert.assertNotNull(pid_2);
    Assert.assertEquals(pid.getPidID(), pid_2.getPidID());
    Assert.assertEquals(uPid.getUrl(), pid_1.getUrl());

    LOG.info("--------------------- FINISHED test_5_UpdatePid ---------------------");
  }
}