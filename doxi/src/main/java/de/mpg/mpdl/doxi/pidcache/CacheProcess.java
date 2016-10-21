package de.mpg.mpdl.doxi.pidcache;

import java.net.URI;
import java.util.Date;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mpg.mpdl.doxi.exception.DoxiException;
import de.mpg.mpdl.doxi.util.PropertyReader;

public class CacheProcess {
  private static final Logger LOG = LoggerFactory.getLogger(CacheProcess.class);

  private final String dummyUrl;
  private final CacheManager cacheManager;
  private final GwdgClient gwdgController;

  public CacheProcess(EntityManager em, GwdgClient gwdgController) {
    this.cacheManager = new CacheManager(em);
    this.gwdgController = gwdgController;
    this.dummyUrl = PropertyReader.getProperty(PropertyReader.DOXI_PID_CACHE_DUMMY_URL);
  }

  public void fill(int anzahl) {
    long current = 0;
    int i = 0;
    while (this.cacheManager.isFull() == false && current != new Date().getTime() && i < anzahl) {
      current = new Date().getTime();
      try {
        final Pid pid = gwdgController.create(URI.create(this.dummyUrl.concat(Long.toString(current))));
        this.cacheManager.add(pid.getPidID());
      } catch (DoxiException e) {
        // TODO
      }
      i++;
    }
  }
}
