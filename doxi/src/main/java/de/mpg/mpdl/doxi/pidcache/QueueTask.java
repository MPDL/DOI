package de.mpg.mpdl.doxi.pidcache;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mpg.mpdl.doxi.controller.GwdgController;
import de.mpg.mpdl.doxi.rest.JerseyApplicationConfig;
import de.mpg.mpdl.doxi.util.PropertyReader;

public class QueueTask extends Thread {
  private static final Logger LOG = LoggerFactory.getLogger(QueueTask.class);

  private boolean signal = false;

  public void run() {
    try {
      this.setName("PidQueue Empty Task");

      final int timeout = Integer.parseInt(PropertyReader.getProperty(PropertyReader.DOXI_PIDCACHE_EMPTY_INTERVAL)) * 1000;
      final int blockSize = Integer.parseInt(PropertyReader.getProperty(PropertyReader.DOXI_PIDCACHE_EMPTY_BLOCKSIZE));

      final EntityManager em = JerseyApplicationConfig.emf.createEntityManager();
      final GwdgController controller = new GwdgController();
      final QueueProcess process = new QueueProcess(em, controller);

      LOG.info("Starting emtpying of pid queue...");

      while (!this.signal) {
        if (controller.serviceAvailable()) {
          em.getTransaction().begin();
          process.empty(blockSize);
          em.getTransaction().commit();
        }
        Thread.sleep(Long.parseLong(Integer.toString(timeout)));
      }
    } catch (Exception e) {
      // TODO
      LOG.error("Error initializing PidQueue Empty Task", e);
    }
    LOG.info("PidQueue Empty Task terminated.");
  }

  public void terminate() {
    LOG.warn("PidQueue Empty Task signalled to terminate.");
    this.signal = true;
  }
}
