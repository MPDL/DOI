package de.mpg.mpdl.doxi.pidcache;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mpg.mpdl.doxi.controller.GwdgController;
import de.mpg.mpdl.doxi.rest.JerseyApplicationConfig;
import de.mpg.mpdl.doxi.util.PropertyReader;

public class CacheTask extends Thread {
  private static final Logger LOG = LoggerFactory.getLogger(CacheTask.class);

  private boolean signal = false;

  public void run() {
    try {
      this.setName("PidCache Refresh Task");

      final int timeout = Integer.parseInt(PropertyReader.getProperty(PropertyReader.DOXI_PIDCACHE_REFRESH_INTERVAL)) * 1000;
      final int blockSize = Integer.parseInt(PropertyReader.getProperty(PropertyReader.DOXI_PIDCACHE_REFRESH_BLOCKSIZE));

      final EntityManager em = JerseyApplicationConfig.emf.createEntityManager();
      final GwdgController controller = new GwdgController();
      final CacheProcess process = new CacheProcess(em, controller);

      LOG.info("Starting refresh of pid cache databases.");

      while (!this.signal) {
        if (controller.serviceAvailable()) {
          em.getTransaction().begin();
          process.fill(blockSize);
          em.getTransaction().commit();
        }
        Thread.sleep(Long.parseLong(Integer.toString(timeout)));
      }
    } catch (Exception e) {
      // TODO
      LOG.error("Error initializing PidCache Refresh Task", e);
    }
    LOG.info("PidCache Refresh Task terminated.");
  }

  public void terminate() {
    LOG.warn("PidCache Refresh Task signalled to terminate.");
    this.signal = true;
  }
}
