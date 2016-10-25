package de.mpg.mpdl.doxi.pidcache;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mpg.mpdl.doxi.rest.JerseyApplicationConfig;

public class InitializerServlet extends HttpServlet {
  private static final Logger LOG = LoggerFactory.getLogger(InitializerServlet.class);
  
  private PidCacheTask pidCacheTask;
  private PidQueueTask pidQueueTask;

  public final void init() throws ServletException {
    super.init();

    this.pidCacheTask = new PidCacheTask();
    this.pidCacheTask.start();

    this.pidQueueTask = new PidQueueTask();
    this.pidQueueTask.start();
  }

  public void destroy() {
    super.destroy();
    
    this.pidCacheTask.terminate();
    this.pidQueueTask.terminate();

    // Close factory - necessary for proper restart
    if (JerseyApplicationConfig.emf != null && JerseyApplicationConfig.emf.isOpen()) {
      LOG.info("closing EntityManagerFactory");
      JerseyApplicationConfig.emf.close();
    }
    
    // Deregister database driver - necessary for proper resource management
    Enumeration<Driver> drivers = DriverManager.getDrivers();
    while (drivers.hasMoreElements()) {
        Driver driver = drivers.nextElement();
        try {
            DriverManager.deregisterDriver(driver);
            LOG.info(String.format("deregistering jdbc driver: %s", driver));
        } catch (SQLException e) {
            LOG.error(String.format("Error deregistering driver %s", driver), e);
        }
    }    
  }
}
