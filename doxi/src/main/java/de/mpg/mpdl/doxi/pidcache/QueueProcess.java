package de.mpg.mpdl.doxi.pidcache;

import java.util.List;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mpg.mpdl.doxi.controller.GwdgController;
import de.mpg.mpdl.doxi.exception.PidNotFoundException;
import de.mpg.mpdl.doxi.model.Pid;

public class QueueProcess {
  private static final Logger LOG = LoggerFactory.getLogger(QueueProcess.class);

  private final QueueManager queueManager;
  private final GwdgController gwdgController;

  public QueueProcess(EntityManager em, GwdgController gwdgController) {
    this.queueManager = new QueueManager(em);
    this.gwdgController = gwdgController;
  }

  public void empty(int anzahl) {
    final List<Pid> pids = this.queueManager.getFirstBlock(anzahl);
    if (pids.size() == 0) {
      return;
    }

    for (Pid pid : pids) {
      try {
        this.gwdgController.updatePid(pid);
        this.queueManager.remove(pid.getPidID());
      } catch (PidNotFoundException e) {
        // TODO
      }
    }
  }
}
