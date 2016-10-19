package de.mpg.mpdl.doxi.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyReader {

  private static Logger logger = LoggerFactory.getLogger(PropertyReader.class);

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
      logger.info("Properties loaded from doxi.properties");
    } catch (IOException e) {
      logger.error("Error while loading properties from doxi.properties", e);
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
      logger.info("Messages loaded from message.properties");
    } catch (IOException e) {
      logger.error("Error while loading messages from message.properties", e);
    }

  }

}
