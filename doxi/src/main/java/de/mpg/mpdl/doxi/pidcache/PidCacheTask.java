package de.mpg.mpdl.doxi.pidcache;

import java.io.File;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mpg.mpdl.doxi.rest.EMF;
import de.mpg.mpdl.doxi.util.PropertyReader;

public class PidCacheTask extends Thread {
  private static final Logger LOG = LoggerFactory.getLogger(PidCacheTask.class);

  private final EntityManager em = EMF.emf.createEntityManager();

  private boolean terminate = false;

  public void run() {
    try {
      this.setName("PidCacheTask");

      final long refreshInterval = Long.parseLong(PropertyReader.getProperty(PropertyReader.DOXI_PID_CACHE_REFRESH_INTERVAL));
      final int blockSize = Integer.parseInt(PropertyReader.getProperty(PropertyReader.DOXI_PID_CACHE_REFRESH_BLOCKSIZE));

      final GwdgClient gwdgClient = new GwdgClient();
      final PidCacheProcess process = new PidCacheProcess(gwdgClient, this.em);

      LOG.info("Starting PidCacheTask");

      while (!this.terminate) {
        if (!existSleepFile())
          if (gwdgClient.serviceAvailable()) {
            LOG.info("Gwdg Service available.");
            process.fill(blockSize);
          } else {
            LOG.warn("Gwdg Service not available.");
          }
        else {
          LOG.warn("Sleep File gesetzt: " + PropertyReader.getProperty(PropertyReader.DOXI_PID_CACHE_CACHE_SLEEP_FILE));
        }
        Thread.sleep(refreshInterval);
      }
    } catch (InterruptedException e) {
      LOG.warn("PidCacheTask InterruptedException angefordert.");
      this.terminate = true;
    } catch (Exception e) {
      LOG.error("PID_CACHE_TASK:\n{}", e);
    } finally {
      if (this.em.isOpen()) {
        this.em.close();
      }
    }
    
    LOG.info("PidCacheTask terminated.");
  }
  
  private boolean existSleepFile() {
    File f = new File(PropertyReader.getProperty(PropertyReader.DOXI_PID_CACHE_CACHE_SLEEP_FILE));
    return f.exists();
  }
}
