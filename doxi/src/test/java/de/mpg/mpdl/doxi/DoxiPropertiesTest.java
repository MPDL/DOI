package de.mpg.mpdl.doxi;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mpg.mpdl.doxi.util.PropertyReader;

public class DoxiPropertiesTest {
  private static final Logger LOG = LoggerFactory.getLogger(DoxiPropertiesTest.class);

  @Before
  public void setUp() throws Exception {}

  @After
  public void tearDown() throws Exception {
  }

  @Ignore
  @Test
  public void test_properties() throws Exception {
    LOG.info("--------------------- STARTING test_properties ---------------------");

    LOG.info(PropertyReader.getProperty(PropertyReader.DOXI_ADMIN_CREATE));
    Assert.assertNotNull(PropertyReader.getProperty(PropertyReader.DOXI_ADMIN_CREATE));
    LOG.info(PropertyReader.getProperty(PropertyReader.DOXI_ADMIN_PASSWORD));
    Assert.assertNotNull(PropertyReader.getProperty(PropertyReader.DOXI_ADMIN_PASSWORD));
    LOG.info(PropertyReader.getProperty(PropertyReader.DOXI_ADMIN_PREFIX));
    Assert.assertNotNull(PropertyReader.getProperty(PropertyReader.DOXI_ADMIN_PREFIX));
    LOG.info(PropertyReader.getProperty(PropertyReader.DOXI_ADMIN_USER));
    Assert.assertNotNull(PropertyReader.getProperty(PropertyReader.DOXI_ADMIN_USER));

    LOG.info(PropertyReader.getProperty(PropertyReader.DOXI_DOI_DATACITE_API_LOGIN_PASSWORD));
    Assert.assertNotNull(PropertyReader.getProperty(PropertyReader.DOXI_DOI_DATACITE_API_LOGIN_PASSWORD));
    LOG.info(PropertyReader.getProperty(PropertyReader.DOXI_DOI_DATACITE_API_LOGIN_USER));
    Assert.assertNotNull(PropertyReader.getProperty(PropertyReader.DOXI_DOI_DATACITE_API_LOGIN_USER));
    LOG.info(PropertyReader.getProperty(PropertyReader.DOXI_DOI_DATACITE_API_TESTMODE));
    Assert.assertNotNull(PropertyReader.getProperty(PropertyReader.DOXI_DOI_DATACITE_API_TESTMODE));
    LOG.info(PropertyReader.getProperty(PropertyReader.DOXI_DOI_DATACITE_API_URL));
    Assert.assertNotNull(PropertyReader.getProperty(PropertyReader.DOXI_DOI_DATACITE_API_URL));

    LOG.info(PropertyReader.getProperty(PropertyReader.DOXI_JDBC_DRIVER));
    Assert.assertNotNull(PropertyReader.getProperty(PropertyReader.DOXI_JDBC_DRIVER));
    LOG.info(PropertyReader.getProperty(PropertyReader.DOXI_JDBC_PASSWORD));
    Assert.assertNotNull(PropertyReader.getProperty(PropertyReader.DOXI_JDBC_PASSWORD));
    LOG.info(PropertyReader.getProperty(PropertyReader.DOXI_JDBC_URL));
    Assert.assertNotNull(PropertyReader.getProperty(PropertyReader.DOXI_JDBC_URL));
    LOG.info(PropertyReader.getProperty(PropertyReader.DOXI_JDBC_USER));
    Assert.assertNotNull(PropertyReader.getProperty(PropertyReader.DOXI_JDBC_USER));

    LOG.info(PropertyReader.getProperty(PropertyReader.DOXI_PID_CACHE_CACHE_SIZE_MAX));
    Assert.assertNotNull(PropertyReader.getProperty(PropertyReader.DOXI_PID_CACHE_CACHE_SIZE_MAX));
    LOG.info(PropertyReader.getProperty(PropertyReader.DOXI_PID_CACHE_DUMMY_URL));
    Assert.assertNotNull(PropertyReader.getProperty(PropertyReader.DOXI_PID_CACHE_DUMMY_URL));
    LOG.info(PropertyReader.getProperty(PropertyReader.DOXI_PID_CACHE_EMPTY_BLOCKSIZE));
    Assert.assertNotNull(PropertyReader.getProperty(PropertyReader.DOXI_PID_CACHE_EMPTY_BLOCKSIZE));
    LOG.info(PropertyReader.getProperty(PropertyReader.DOXI_PID_CACHE_EMPTY_INTERVAL));
    Assert.assertNotNull(PropertyReader.getProperty(PropertyReader.DOXI_PID_CACHE_EMPTY_INTERVAL));
    LOG.info(PropertyReader.getProperty(PropertyReader.DOXI_PID_CACHE_REFRESH_BLOCKSIZE));
    Assert.assertNotNull(PropertyReader.getProperty(PropertyReader.DOXI_PID_CACHE_REFRESH_BLOCKSIZE));
    LOG.info(PropertyReader.getProperty(PropertyReader.DOXI_PID_CACHE_REFRESH_INTERVAL));
    Assert.assertNotNull(PropertyReader.getProperty(PropertyReader.DOXI_PID_CACHE_REFRESH_INTERVAL));

    LOG.info(PropertyReader.getProperty(PropertyReader.DOXI_PID_GWDG_SERVICE_CREATE_PATH));
    Assert.assertNotNull(PropertyReader.getProperty(PropertyReader.DOXI_PID_GWDG_SERVICE_CREATE_PATH));
    LOG.info(PropertyReader.getProperty(PropertyReader.DOXI_PID_GWDG_SERVICE_SEARCH_PATH));
    Assert.assertNotNull(PropertyReader.getProperty(PropertyReader.DOXI_PID_GWDG_SERVICE_SEARCH_PATH));
    LOG.info(PropertyReader.getProperty(PropertyReader.DOXI_PID_GWDG_SERVICE_UPDATE_PATH));
    Assert.assertNotNull(PropertyReader.getProperty(PropertyReader.DOXI_PID_GWDG_SERVICE_UPDATE_PATH));
    LOG.info(PropertyReader.getProperty(PropertyReader.DOXI_PID_GWDG_SERVICE_URL));
    Assert.assertNotNull(PropertyReader.getProperty(PropertyReader.DOXI_PID_GWDG_SERVICE_URL));
    LOG.info(PropertyReader.getProperty(PropertyReader.DOXI_PID_GWDG_SERVICE_VIEW_PATH));
    Assert.assertNotNull(PropertyReader.getProperty(PropertyReader.DOXI_PID_GWDG_SERVICE_VIEW_PATH));
    LOG.info(PropertyReader.getProperty(PropertyReader.DOXI_PID_GWDG_TIMEOUT));
    Assert.assertNotNull(PropertyReader.getProperty(PropertyReader.DOXI_PID_GWDG_TIMEOUT));
    LOG.info(PropertyReader.getProperty(PropertyReader.DOXI_PID_GWDG_USER_LOGIN));
    Assert.assertNotNull(PropertyReader.getProperty(PropertyReader.DOXI_PID_GWDG_USER_LOGIN));
    LOG.info(PropertyReader.getProperty(PropertyReader.DOXI_PID_GWDG_USER_PASSWORD));
    Assert.assertNotNull(PropertyReader.getProperty(PropertyReader.DOXI_PID_GWDG_USER_PASSWORD));

    LOG.info("--------------------- FINISHED test_properties ---------------------");
  }
}
