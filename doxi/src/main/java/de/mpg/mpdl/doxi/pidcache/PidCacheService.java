package de.mpg.mpdl.doxi.pidcache;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import de.mpg.mpdl.doxi.util.PropertyReader;

public class PidCacheService {
  private final int sizeMax;
  private final PidCacheDAO pidCacheDAO;

  public PidCacheService(EntityManager em) {
    this.pidCacheDAO = new PidCacheDAO(em);
    this.sizeMax =
        Integer.parseInt(PropertyReader.getProperty(PropertyReader.DOXI_PID_CACHE_CACHE_SIZE_MAX));
  }

  public void add(PidID pidID) throws PidCacheServiceException {
    try {
      final PidCache pidCache = new PidCache(pidID, new Date());
      this.pidCacheDAO.create(pidCache);
    } catch (Exception e) {
      throw new PidCacheServiceException(e);
    }
  }

  public void remove(PidID pidID) throws PidCacheServiceException {
    try {
      final PidCache pidCache = this.pidCacheDAO.find(pidID);
      if (pidCache != null) {
        this.pidCacheDAO.remove(pidCache);
      }
    } catch (Exception e) {
      throw new PidCacheServiceException(e);
    }
  }

  public PidID getFirst() throws PidCacheServiceException {
    try {
      final PidCache pidCache = this.pidCacheDAO.getFirst();
      if (pidCache != null) {
        return pidCache.getID();
      }
    } catch (NoResultException e) {
      return null;
    } catch (Exception e) {
      throw new PidCacheServiceException(e);
    }
    
    return null;
  }

  public long getSize() throws PidCacheServiceException {
    try {
      return this.pidCacheDAO.getSize();
    } catch (Exception e) {
      throw new PidCacheServiceException(e);
    }
  }

  public boolean isFull() throws PidCacheServiceException {
    try {
      return (this.sizeMax == getSize());
    } catch (Exception e) {
      throw new PidCacheServiceException(e);
    }
  }

  public boolean isEmpty() throws PidCacheServiceException {
    try {
      return (0 == getSize());
    } catch (Exception e) {
      throw new PidCacheServiceException(e);
    }
  }

  public int getSizeMax() {
    return this.sizeMax;
  }
}
