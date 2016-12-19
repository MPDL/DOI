package de.mpg.mpdl.doxi.view;

import java.util.List;

import javax.persistence.EntityManager;

import de.mpg.mpdl.doxi.pidcache.PidCache;
import de.mpg.mpdl.doxi.pidcache.PidCacheDAO;
import de.mpg.mpdl.doxi.util.EMF;

public class ViewPidCacheDB {

  public List<PidCache> pidCacheList;

  public ViewPidCacheDB() {
    EntityManager em = EMF.emf.createEntityManager();
    
    PidCacheDAO pidCacheDAO = new PidCacheDAO(em);
    setPidCacheList(pidCacheDAO.getFirst(100));

    em.close();
  }

  public List<PidCache> getPidCacheList() {
    return pidCacheList;
  }

  public void setPidCacheList(List<PidCache> pidCacheList) {
    this.pidCacheList = pidCacheList;
  }
}
