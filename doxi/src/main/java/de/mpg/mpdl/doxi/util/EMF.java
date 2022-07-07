package de.mpg.mpdl.doxi.util;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class EMF implements ServletContextListener {
  private static final Logger LOG = LoggerFactory.getLogger(EMF.class);

  public static EntityManagerFactory emf;

  private static Map<String, String> jpaDBProperties = new HashMap<String, String>();

  static {
    jpaDBProperties.put("jakarta.persistence.jdbc.driver",
        PropertyReader.getProperty(PropertyReader.DOXI_JDBC_DRIVER));
    jpaDBProperties.put("jakarta.persistence.jdbc.url",
        PropertyReader.getProperty(PropertyReader.DOXI_JDBC_URL));
    jpaDBProperties.put("jakarta.persistence.jdbc.user",
        PropertyReader.getProperty(PropertyReader.DOXI_JDBC_USER));
    jpaDBProperties.put("jakarta.persistence.jdbc.password",
        PropertyReader.getProperty(PropertyReader.DOXI_JDBC_PASSWORD));
  }

  @Override
  public void contextInitialized(ServletContextEvent sce) {
	  LOG.info("+++ ServletContextListener : contextInitialized - Bridging JUL to Log4j");
	  Log4jBridgeHandler.install(true, "jul-log4j-bridge", true);
	  //SLF4jBridgeHandler.install();
    
	  LOG.info("+++ ServletContextListener : contextInitialized - Inititalizing EMF");
    emf = Persistence.createEntityManagerFactory("default", jpaDBProperties);
    LOG.info("+++ ServletContextListener : contextInitialized - Init EMF done.");
    
    
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    LOG.info("+++ ServletContextListener : contextDestroyed - Closing EMF");
    emf.close();
    LOG.info("+++ ServletContextListener : contextDestroyed - Closed EMF DONE.");
    
    LOG.info("+++ ServletContextListener : contextDestroyed - Deregistering JDBC Driver");
    Enumeration<Driver> drivers = DriverManager.getDrivers();
    while (drivers.hasMoreElements()) {
      Driver driver = drivers.nextElement();
      try {
        DriverManager.deregisterDriver(driver);
        LOG.info("+++ ServletContextListener : contextDestroyed - Deregistering JDBC Driver DONE.");
      } catch (SQLException e) {
        LOG.error("Error deregistering driver: {}\n{}", driver, e);
      }
    }    
  }
}
