package de.mpg.mpdl.doxi.pidcache;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

public class QueueManager {
  private final PidQueueDAO pidQueueDAO;

  public QueueManager(EntityManager em) {
    this.pidQueueDAO = new PidQueueDAO(em);
  }

  public void add(Pid pid) {
    final PidQueue pidQueue = new PidQueue(pid.getPidID(), pid.getUrl(), new Date());
    this.pidQueueDAO.create(pidQueue);
  }

  public void remove(PidID pidID) {
    final PidQueue pidQueue = this.pidQueueDAO.find(pidID);
    if (pidQueue != null) {
      this.pidQueueDAO.remove(pidQueue);
    }
  }

  public Pid retrieve(PidID pidID) {
    final PidQueue pidQueue = this.pidQueueDAO.find(pidID);
    if (pidQueue != null) {
      return new Pid(pidQueue.getID(), pidQueue.getUrl());
    }
    
    return null;
  }
  
  public Pid getFirst() {
    final PidQueue pidQueue = this.pidQueueDAO.getFirst();
    if (pidQueue != null) {
      return new Pid(pidQueue.getID(), pidQueue.getUrl());
    }
    
    return null;
  }
  
  public List<Pid> getFirstBlock(int size) {
    final List<Pid> pids = new ArrayList<Pid>();

    final List<PidQueue> list = this.pidQueueDAO.getFirst(size);

    for (PidQueue pidQueue : list) {
      pids.add(new Pid(pidQueue.getID(), pidQueue.getUrl()));
    }
    
    return pids;
  }
  
  public Pid search(URI url) {
    final PidQueue pidQueue = this.pidQueueDAO.findByUrl(url);
    if (pidQueue != null) {
      return new Pid(pidQueue.getID(), pidQueue.getUrl());
    }
    
    return null;
  }
  
  public int size() {
    return this.pidQueueDAO.getSize();
  }
  
  public boolean isEmpty() throws Exception {
    return this.size() == 0;
  }
}
