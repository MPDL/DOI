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
  private final EntityManager em;

  public PidQueueProcess(GwdgClient gwdgClient, EntityManager em) {
    this.gwdgClient = gwdgClient;
    this.em = em;
    this.pidQueueService = new PidQueueService(em);
  }

  public void empty(int anzahl) {
    final List<Pid> pids = this.pidQueueService.getFirstBlock(anzahl);
    if (pids.size() == 0) {
      return;
    }

    for (Pid pid : pids) {
      try {
        this.em.getTransaction().begin();
        this.gwdgClient.update(pid);
        this.pidQueueService.remove(pid.getPidID());
        this.em.getTransaction().commit();
      } catch (PidNotFoundException e) {
        // TODO
      }
    }
  }
}
