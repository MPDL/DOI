package de.mpg.mpdl.doxi.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyReader {
  private static final Logger LOG = LoggerFactory.getLogger(PropertyReader.class);

  private static final String PROPERTIES_FILE = "/doxi.properties";
  private static final String MESSAGES_FILE = "/messages.properties";
  
  public static final String DOXI_ADMIN_CREATE = "doxi.admin.create";
  public static final String DOXI_ADMIN_PASSWORD = "doxi.admin.password";
  public static final String DOXI_ADMIN_PREFIX = "doxi.admin.prefix";
  public static final String DOXI_ADMIN_USER = "doxi.admin.user";
  
  public static final String DOXI_DOI_DATACITE_API_LOGIN_PASSWORD = "doxi.doi.datacite.api.login.password";
  public static final String DOXI_DOI_DATACITE_API_LOGIN_USER = "doxi.doi.datacite.api.login.user";
  public static final String DOXI_DOI_DATACITE_API_TESTMODE = "doxi.doi.datacite.api.testmode";
  public static final String DOXI_DOI_DATACITE_API_URL = "doxi.doi.datacite.api.url";
  
  public static final String DOXI_JDBC_DRIVER = "doxi.jdbc.driver";
  public static final String DOXI_JDBC_PASSWORD = "doxi.jdbc.password";
  public static final String DOXI_JDBC_URL = "doxi.jdbc.url";
  public static final String DOXI_JDBC_USER = "doxi.jdbc.user";
  
  public static final String DOXI_PID_CACHE_CACHE_SIZE_MAX = "doxi.pid.cache.cache.size.max";
  public static final String DOXI_PID_CACHE_CACHE_SLEEP_FILE = "doxi.pid.cache.cache.sleep.file";
  public static final String DOXI_PID_CACHE_DUMMY_URL = "doxi.pid.cache.dummy.url";
  public static final String DOXI_PID_CACHE_EMPTY_BLOCKSIZE = "doxi.pid.cache.empty.blocksize";
  public static final String DOXI_PID_CACHE_EMPTY_INTERVAL = "doxi.pid.cache.empty.interval";
  public static final String DOXI_PID_CACHE_QUEUE_SLEEP_FILE = "doxi.pid.cache.queue.sleep.file";
  public static final String DOXI_PID_CACHE_REFRESH_BLOCKSIZE = "doxi.pid.cache.refresh.blocksize";
  public static final String DOXI_PID_CACHE_REFRESH_INTERVAL = "doxi.pid.cache.refresh.interval";  
  
  public static final String DOXI_PID_GWDG_SERVICE_URL = "doxi.pid.gwdg.service.url";
  public static final String DOXI_PID_GWDG_SERVICE_SUFFIX = "doxi.pid.gwdg.service.suffix";
  public static final String DOXI_PID_GWDG_TIMEOUT = "doxi.pid.gwdg.timeout";
  public static final String DOXI_PID_GWDG_USER_LOGIN = "doxi.pid.gwdg.user.login";
  public static final String DOXI_PID_GWDG_USER_PASSWORD = "doxi.pid.gwdg.user.password";
  
  private static Properties properties = null;
  private static Properties messages = null;

  public static String getProperty(String key) {
    if (properties == null) {
      loadProperties();
    }
    
    return properties.getProperty(key);
  }

  private static void loadProperties() {
    try {
      // First try to load from from internal
      InputStream is = PropertyReader.class.getResourceAsStream(PROPERTIES_FILE);
      if (is == null) {
        // then from tomcat's conf dir
        String tomcatHome = System.getProperty("catalina.base");
        String path = tomcatHome + "/conf" + PROPERTIES_FILE;
        is = new FileInputStream(path);
      }
      properties = new Properties();
      properties.load(is);
      LOG.info("Properties loaded.");
    } catch (IOException e) {
      LOG.error("PROPERTY READER: {}\n{}", PROPERTIES_FILE, e);
      throw new IllegalArgumentException("PropertyReader: " + PROPERTIES_FILE + " not found");
    }

  }

  public static String getMessage(String key) {
    if (messages == null) {
      loadMessages();
    }
    
    return messages.getProperty(key);
  }

  private static void loadMessages() {
    try {
      InputStream is = PropertyReader.class.getResourceAsStream(MESSAGES_FILE);
      messages = new Properties();
      messages.load(is);
      LOG.info("Messages loaded.");
    } catch (IOException e) {
      LOG.error("PROPERTY READER: {}\n{}", MESSAGES_FILE, e);
      throw new IllegalArgumentException("PropertyReader: " + MESSAGES_FILE + " not found");
    }
  }
}
