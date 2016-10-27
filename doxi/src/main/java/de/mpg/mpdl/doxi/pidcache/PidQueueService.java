package de.mpg.mpdl.doxi.pidcache;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

public class PidQueueService {
  private final PidQueueDAO pidQueueDAO;

  public PidQueueService(EntityManager em) {
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

  public Pid update(Pid pid) {
    final PidQueue pidQueue = this.pidQueueDAO.find(pid.getPidID());
    if (pidQueue != null) {
      pidQueue.setUrl(pid.getUrl());
      this.pidQueueDAO.update(pidQueue);
      return pid;
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
    try {
      final PidQueue pidQueue = this.pidQueueDAO.findByUrl(url);
      if (pidQueue != null) {
        return new Pid(pidQueue.getID(), pidQueue.getUrl());
      }
    } catch (NoResultException e) {
      return null;
    }

    return null;
  }

  public long getSize() {
    return this.pidQueueDAO.getSize();
  }

  public boolean isEmpty() {
    return this.getSize() == 0;
  }
}
