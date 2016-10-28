package de.mpg.mpdl.doxi.pidcache;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mpg.mpdl.doxi.rest.EMF;
import de.mpg.mpdl.doxi.util.PropertyReader;

public class PidQueueTask extends Thread {
  private static final Logger LOG = LoggerFactory.getLogger(PidQueueTask.class);

  private final EntityManager em = EMF.emf.createEntityManager();
  
  private boolean terminate = false;

  public void run() {
    try {
      this.setName("PidQueueTask");

      final long emptyInterval =
          Long.parseLong(PropertyReader.getProperty(PropertyReader.DOXI_PID_CACHE_EMPTY_INTERVAL));
      final int blockSize = Integer
          .parseInt(PropertyReader.getProperty(PropertyReader.DOXI_PID_CACHE_EMPTY_BLOCKSIZE));

      final GwdgClient gwdgClient = new GwdgClient();
      final PidQueueProcess process = new PidQueueProcess(gwdgClient, this.em);

      LOG.info("Starting PidQueueTask");

      while (!this.terminate) {
        if (gwdgClient.serviceAvailable()) {
          LOG.info("Gwdg Service available.");
          process.empty(blockSize);
        } else {
          LOG.warn("Gwdg Service not available.");
        }
        Thread.sleep(emptyInterval);
      }
    } catch (InterruptedException e) {
      LOG.warn("PidQueueTask InterruptedException angefordert.");
      this.terminate = true;
    } catch (Exception e) {
      LOG.error("PID_QUEUE_TASK:\n{}", e);
    } finally {
      if (this.em.isOpen()) {
        this.em.close();
      }
    }
    
    LOG.info("PidQueueTask terminated.");
  }
}
