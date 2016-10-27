package de.mpg.mpdl.doxi.pidcache;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mpg.mpdl.doxi.rest.JerseyApplicationConfig;
import de.mpg.mpdl.doxi.util.PropertyReader;

public class PidCacheTask extends Thread {
  private static final Logger LOG = LoggerFactory.getLogger(PidCacheTask.class);

  private boolean terminate = false;

  public void run() {
    try {
      this.setName("PidCacheTask");

      final long refreshInterval = Long
          .parseLong(PropertyReader.getProperty(PropertyReader.DOXI_PID_CACHE_REFRESH_INTERVAL));
      final int blockSize = Integer
          .parseInt(PropertyReader.getProperty(PropertyReader.DOXI_PID_CACHE_REFRESH_BLOCKSIZE));

      final GwdgClient gwdgClient = new GwdgClient();
      final EntityManager em = JerseyApplicationConfig.emf.createEntityManager();
      final PidCacheProcess process = new PidCacheProcess(gwdgClient, em);

      LOG.info("Starting PidCacheTask");

      while (!this.terminate) {
        if (gwdgClient.serviceAvailable()) {
          LOG.info("Gwdg Service available.");
          process.fill(blockSize);
        } else {
          LOG.warn("Gwdg Service not available.");
        }
        Thread.sleep(refreshInterval);
      }
    } catch (InterruptedException e) {
      LOG.warn("PidCacheTask InterruptedException angefordert.");
      this.terminate = true;
    } catch (Exception e) {
      LOG.error("ERROR " + e);
    }
    
    LOG.info("PidCacheTask terminated.");
  }
}
