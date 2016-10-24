package de.mpg.mpdl.doxi.pidcache;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mpg.mpdl.doxi.rest.JerseyApplicationConfig;
import de.mpg.mpdl.doxi.util.PropertyReader;

public class PidQueueTask extends Thread {
  private static final Logger LOG = LoggerFactory.getLogger(PidQueueTask.class);

  private boolean signal = false;

  public void run() {
    try {
      this.setName("PidQueue Empty Task");

      final long emptyInterval = Long.parseLong(PropertyReader.getProperty(PropertyReader.DOXI_PID_CACHE_EMPTY_INTERVAL));
      final int blockSize = Integer.parseInt(PropertyReader.getProperty(PropertyReader.DOXI_PID_CACHE_EMPTY_BLOCKSIZE));

      final EntityManager em = JerseyApplicationConfig.emf.createEntityManager();
      final GwdgClient gwdgClient = new GwdgClient();
      final PidQueueProcess process = new PidQueueProcess(em, gwdgClient);

      LOG.info("Starting emtpying of pid queue...");

      while (!this.signal) {
        if (gwdgClient.serviceAvailable()) {
          em.getTransaction().begin();
          process.empty(blockSize);
          em.getTransaction().commit();
        }
        Thread.sleep(emptyInterval);
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
