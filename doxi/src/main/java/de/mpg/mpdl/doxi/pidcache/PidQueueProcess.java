package de.mpg.mpdl.doxi.pidcache;

import java.util.List;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mpg.mpdl.doxi.exception.PidNotFoundException;

public class PidQueueProcess {
  private static final Logger LOG = LoggerFactory.getLogger(PidQueueProcess.class);

  private final PidQueueService pidQueueService;
  private final GwdgClient gwdgClient;

  public PidQueueProcess(EntityManager em, GwdgClient gwdgClient) {
    this.pidQueueService = new PidQueueService(em);
    this.gwdgClient = gwdgClient;
  }

  public void empty(int anzahl) {
    final List<Pid> pids = this.pidQueueService.getFirstBlock(anzahl);
    if (pids.size() == 0) {
      return;
    }

    for (Pid pid : pids) {
      try {
        this.gwdgClient.update(pid);
        this.pidQueueService.remove(pid.getPidID());
      } catch (PidNotFoundException e) {
        // TODO
      }
    }
  }
}
