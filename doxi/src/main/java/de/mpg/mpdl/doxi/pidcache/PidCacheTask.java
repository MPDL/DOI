package de.mpg.mpdl.doxi.pidcache;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mpg.mpdl.doxi.rest.JerseyApplicationConfig;
import de.mpg.mpdl.doxi.util.PropertyReader;

public class PidCacheTask extends Thread {
  private static final Logger LOG = LoggerFactory.getLogger(PidCacheTask.class);

  private boolean signal = false;

  public void run() {
    try {
      this.setName("PidCache Refresh Task");

      final long refreshInterval = Long.parseLong(PropertyReader.getProperty(PropertyReader.DOXI_PID_CACHE_REFRESH_INTERVAL));
      final int blockSize = Integer.parseInt(PropertyReader.getProperty(PropertyReader.DOXI_PID_CACHE_REFRESH_BLOCKSIZE));

      final EntityManager em = JerseyApplicationConfig.emf.createEntityManager();
      final GwdgClient gwdgClient = new GwdgClient();
      final PidCacheProcess process = new PidCacheProcess(gwdgClient, em);

      LOG.info("Starting refresh of pid cache databases.");

      while (!this.signal) {
        if (gwdgClient.serviceAvailable()) {
          process.fill(blockSize);
        }
        Thread.sleep(refreshInterval);
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
