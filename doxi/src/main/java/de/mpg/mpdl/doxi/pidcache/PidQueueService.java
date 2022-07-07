package de.mpg.mpdl.doxi.pidcache;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.EntityManager;

import de.mpg.mpdl.doxi.pidcache.model.Pid;
import de.mpg.mpdl.doxi.pidcache.model.PidID;

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

  public PidQueue retrieve(PidID pidID) {
    return this.pidQueueDAO.find(pidID);
  }

  public List<Pid> getFirstBlock(int size) {
    final List<Pid> pids = new ArrayList<Pid>();

    final List<PidQueue> list = this.pidQueueDAO.getFirst(size);

    for (PidQueue pidQueue : list) {
      pids.add(new Pid(pidQueue.getID(), pidQueue.getUrl()));
    }

    return pids;
  }

  public List<String> search(URI url) {
    List<PidQueue> list = this.pidQueueDAO.findByUrl(url);
    List<String> result = new ArrayList<String>();

    for (PidQueue pidQueue : list) {
      result.add(pidQueue.getUrl().toString());
    }

    return result;
  }

  public long getSize() {
    return this.pidQueueDAO.getSize();
  }

  public boolean isEmpty() {
    return this.getSize() == 0;
  }
}
