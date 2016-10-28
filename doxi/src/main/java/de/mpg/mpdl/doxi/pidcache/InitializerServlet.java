package de.mpg.mpdl.doxi.pidcache;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
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

    this.pidCacheTask.interrupt();
    this.pidQueueTask.interrupt();

//    // TODO sollte eigentlich JerseyApplicationConfig tun
//    // Close factory - necessary for proper restart
//    if (JerseyApplicationConfig.emf != null && JerseyApplicationConfig.emf.isOpen()) {
//      LOG.info("closing EntityManagerFactory");
//      JerseyApplicationConfig.emf.close();
//    }

    // TODO sollte eigentlich JerseyApplicationConfig tun
    Enumeration<Driver> drivers = DriverManager.getDrivers();
    while (drivers.hasMoreElements()) {
      Driver driver = drivers.nextElement();
      try {
        DriverManager.deregisterDriver(driver);
        LOG.info("deregistering jdbc driver: {}", driver);
      } catch (SQLException e) {
        LOG.error("Error deregistering driver: {}\n{}", driver, e);
      }
    }
  }
}
