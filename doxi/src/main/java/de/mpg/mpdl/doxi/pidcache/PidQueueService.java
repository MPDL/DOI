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

  public void add(Pid pid) throws PidQueueServiceException {
    try {
      final PidQueue pidQueue = new PidQueue(pid.getPidID(), pid.getUrl(), new Date());
      this.pidQueueDAO.create(pidQueue);
    } catch (Exception e) {
      throw new PidQueueServiceException(e);
    }
  }

  public void remove(PidID pidID) throws PidQueueServiceException {
    try {
      final PidQueue pidQueue = this.pidQueueDAO.find(pidID);
      if (pidQueue != null) {
        this.pidQueueDAO.remove(pidQueue);
      }
    } catch (Exception e) {
      throw new PidQueueServiceException(e);
    }
  }

  public Pid retrieve(PidID pidID) throws PidQueueServiceException {
    try {
      final PidQueue pidQueue = this.pidQueueDAO.find(pidID);
      if (pidQueue != null) {
        return new Pid(pidQueue.getID(), pidQueue.getUrl());
      }
    } catch (Exception e) {
      throw new PidQueueServiceException(e);
    }

    return null;
  }

  public Pid update(Pid pid) throws PidQueueServiceException {
    try {
      final PidQueue pidQueue = this.pidQueueDAO.find(pid.getPidID());
      if (pidQueue != null) {
        pidQueue.setUrl(pid.getUrl());
        this.pidQueueDAO.update(pidQueue);
        return pid;
      }
    } catch (Exception e) {
      throw new PidQueueServiceException(e);
    }

    return null;
  }

  public List<Pid> getFirstBlock(int size) throws PidQueueServiceException {
    final List<Pid> pids = new ArrayList<Pid>();

    try {
      final List<PidQueue> list = this.pidQueueDAO.getFirst(size);

      for (PidQueue pidQueue : list) {
        pids.add(new Pid(pidQueue.getID(), pidQueue.getUrl()));
      }
    } catch (Exception e) {
      throw new PidQueueServiceException(e);
    }

    return pids;
  }

  public Pid search(URI url) throws PidQueueServiceException {
    try {
      final PidQueue pidQueue = this.pidQueueDAO.findByUrl(url);
      if (pidQueue != null) {
        return new Pid(pidQueue.getID(), pidQueue.getUrl());
      }
    } catch (NoResultException e) {
      return null;
    } catch (Exception e) {
      throw new PidQueueServiceException(e);
    }

    return null;
  }

  public long getSize() throws PidQueueServiceException {
    try {
      return this.pidQueueDAO.getSize();
    } catch (Exception e) {
      throw new PidQueueServiceException(e);
    }
  }

  public boolean isEmpty() throws PidQueueServiceException {
    try {
      return this.getSize() == 0;
    } catch (Exception e) {
      throw new PidQueueServiceException(e);
    }
  }
}
