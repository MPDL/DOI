package de.mpg.mpdl.doxi.view;

import java.util.List;

import javax.persistence.EntityManager;

import de.mpg.mpdl.doxi.pidcache.PidQueue;
import de.mpg.mpdl.doxi.pidcache.PidQueueDAO;
import de.mpg.mpdl.doxi.util.EMF;

public class ViewPidQueueDB {

  public List<PidQueue> pidQueueList;

  public ViewPidQueueDB() {
    EntityManager em = EMF.emf.createEntityManager();
    
    PidQueueDAO pidQueueDAO = new PidQueueDAO(em);
    setPidQueueList(pidQueueDAO.getFirst(100));

    em.close();
  }

  public List<PidQueue> getPidQueueList() {
    return pidQueueList;
  }

  public void setPidQueueList(List<PidQueue> pidQueueList) {
    this.pidQueueList = pidQueueList;
  }
}
