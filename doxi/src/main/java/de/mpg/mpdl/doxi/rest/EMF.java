package de.mpg.mpdl.doxi.rest;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mpg.mpdl.doxi.util.PropertyReader;

@WebListener
public class EMF implements ServletContextListener {
  private static final Logger LOG = LoggerFactory.getLogger(EMF.class);

  public static EntityManagerFactory emf;

  private static Map<String, String> jpaDBProperties = new HashMap<String, String>();

  static {
    jpaDBProperties.put("javax.persistence.jdbc.driver",
        PropertyReader.getProperty(PropertyReader.DOXI_JDBC_DRIVER));
    jpaDBProperties.put("javax.persistence.jdbc.url",
        PropertyReader.getProperty(PropertyReader.DOXI_JDBC_URL));
    jpaDBProperties.put("javax.persistence.jdbc.user",
        PropertyReader.getProperty(PropertyReader.DOXI_JDBC_USER));
    jpaDBProperties.put("javax.persistence.jdbc.password",
        PropertyReader.getProperty(PropertyReader.DOXI_JDBC_PASSWORD));
  }

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    LOG.info("+++ ServletContextListener : contextInitialized - Inititalizing EMF");
    emf = Persistence.createEntityManagerFactory("default", jpaDBProperties);
    LOG.info("+++ ServletContextListener : contextInitialized - Init EMF done.");
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    LOG.info("+++ ServletContextListener : contextDestroyed - Closing EMF");
    emf.close();
    LOG.info("+++ ServletContextListener : contextDestroyed - Closed EMF done.");
  }
}
