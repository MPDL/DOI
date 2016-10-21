package de.mpg.mpdl.doxi.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyReader {
  private static final Logger LOG = LoggerFactory.getLogger(PropertyReader.class);

  public static final String DOXI_ADMIN_CREATE = "doxi.admin.create";
  public static final String DOXI_ADMIN_PASSWORD = "doxi.admin.password";
  public static final String DOXI_ADMIN_PREFIX = "doxi.admin.prefix";
  public static final String DOXI_ADMIN_USER = "doxi.admin.user";
  
  public static final String DOXI_DOI_DATACITE_API_LOGIN_PASSWORD = "doxi.doi.datacite.api.login.password";
  public static final String DOXI_DOI_DATACITE_API_LOGIN_USER = "doxi.doi.datacite.api.login.user";
  public static final String DOXI_DOI_DATACITE_API_TESTMODE = "doxi.doi.datacite.api.testmode";
  public static final String DOXI_DOI_DATACITE_API_URL = "doxi.datacite.api.url";
  
  public static final String DOXI_JDBC_DRIVER = "doxi.jdbc.driver";
  public static final String DOXI_JDBC_PASSWORD = "doxi.jdbc.password";
  public static final String DOXI_JDBC_URL = "doxi.jdbc.url";
  public static final String DOXI_JDBC_USER = "doxi.jdbc.user";
  
  public static final String DOXI_PIDCACHE_CACHE_SIZE_MAX = "doxi.pidcache.cache.size.max";
  public static final String DOXI_PIDCACHE_DUMMY_URL = "doxi.pidcache.dummy.url";
  public static final String DOXI_PIDCACHE_EMPTY_INTERVAL = "doxi.pidcache.empty.interval";
  public static final String DOXI_PIDCACHE_EMPTY_BLOCKSIZE = "doxi.pidcache.empty.blocksize";
  public static final String DOXI_PIDCACHE_REFRESH_INTERVAL = "doxi.pidcache.refresh.interval";
  public static final String DOXI_PIDCACHE_REFRESH_BLOCKSIZE = "doxi.pidcache.refresh.blocksize";
//  public static final String DOXI_PIDCACHE_USER_NAME = "doxi.pidcache.user.name";
//  public static final String DOXI_PIDCACHE_USER_PASSWORD = "doxi.pidcache.user.password";
  
//  public static final String DOXI_PID_HANDLES_ACTIVATED = "doxi.pid.handles.activated";
  
//  public static final String DOXI_PID_PIDCACHE_SERVICE_URL = "doxi.pid.pidcache.service.url";
  
  public static final String DOXI_PID_GWDG_SERVICE_URL = "doxi.pid.gwdg.service.url";
//public static final String DOXI_PID_GWDG_TIMEOUT = "doxi.pid.gwdg.timeout";
  public static final String DOXI_PID_GWDG_USER_LOGIN = "doxi.pid.gwdg.user.login";
  public static final String DOXI_PID_GWDG_USER_PASSWORD = "doxi.pid.gwdg.user.password";
  public static final String DOXI_PID_GWDG_SERVICE_CREATE_PATH = "doxi.pid.gwdg.service.create.path";
  public static final String DOXI_PID_GWDG_SERVICE_DELETE_PATH = "doxi.pid.gwdg.service.delete.path";
  public static final String DOXI_PID_GWDG_SERVICE_SEARCH_PATH = "doxi.pid.gwdg.service.search.path";
  public static final String DOXI_PID_GWDG_SERVICE_UPDATE_PATH = "doxi.pid.gwdg.service.update.path";
  public static final String DOXI_PID_GWDG_SERVICE_VIEW_PATH = "doxi.pid.gwdg.service.view.path";
  
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
      InputStream is = PropertyReader.class.getResourceAsStream("/doxi.properties");
      if (is == null) {
        // then from tomcat's conf dir
        String tomcatHome = System.getProperty("catalina.base");
        String path = tomcatHome + "/conf/doxi.properties";
        is = new FileInputStream(path);
      }
      properties = new Properties();
      properties.load(is);
      LOG.info("Properties loaded from doxi.properties");
    } catch (IOException e) {
      LOG.error("Error while loading properties from doxi.properties", e);
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
      InputStream is = PropertyReader.class.getResourceAsStream("/message.properties");
      messages = new Properties();
      messages.load(is);
      LOG.info("Messages loaded from message.properties");
    } catch (IOException e) {
      LOG.error("Error while loading messages from message.properties", e);
    }
  }
}
