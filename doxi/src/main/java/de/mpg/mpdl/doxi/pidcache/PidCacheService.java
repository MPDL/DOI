package de.mpg.mpdl.doxi.pidcache;

import java.util.Date;

import javax.persistence.EntityManager;

import de.mpg.mpdl.doxi.util.PropertyReader;

public class PidCacheService {
  private final int sizeMax;
  private final PidCacheDAO pidCacheDAO;

  public PidCacheService(EntityManager em) {
    this.pidCacheDAO = new PidCacheDAO(em);
    this.sizeMax = Integer.parseInt(PropertyReader.getProperty(PropertyReader.DOXI_PID_CACHE_CACHE_SIZE_MAX));
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

  public long size() {
    return this.pidCacheDAO.getSize();
  }

  public boolean isFull() {
    return (this.sizeMax == size());
  }
  
  public int getSizeMax() {
    return this.sizeMax;
  }
}
