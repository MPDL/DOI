package de.mpg.mpdl.doxi.pidcache;

import java.util.Date;

import javax.persistence.EntityManager;

import de.mpg.mpdl.doxi.model.PidID;
import de.mpg.mpdl.doxi.util.PropertyReader;

public class CacheManager {
  private final int sizeMax;
  private final PidCacheDAO pidCacheDAO;

  public CacheManager(EntityManager em) {
    this.sizeMax = Integer.parseInt(PropertyReader.getProperty(PropertyReader.DOXI_PIDCACHE_CACHE_SIZE_MAX));
    this.pidCacheDAO = new PidCacheDAO(em);
  }

  public void add(PidID pidID) {
    final PidCache pidCache = new PidCache(pidID, new Date());
    this.pidCacheDAO.create(pidCache);
  }

  public void remove(PidID pidID) {
    final PidCache pidCache = this.pidCacheDAO.find(pidID);
    if (pidCache != null) {
      this.pidCacheDAO.remove(pidCache);
    }
  }

  public PidID getFirst() {
    final PidCache pidCache = this.pidCacheDAO.getFirst();
    if (pidCache != null) {
      return pidCache.getID();
    }

    return null;
  }

  public int size() {
    return this.pidCacheDAO.getSize();
  }

  public boolean isFull() {
    return (this.sizeMax == size());
  }
  
  public int getSizeMax() {
    return this.sizeMax;
  }
}
