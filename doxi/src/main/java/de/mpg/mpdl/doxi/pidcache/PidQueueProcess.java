package de.mpg.mpdl.doxi.pidcache;

import java.util.List;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mpg.mpdl.doxi.exception.DoxiException;

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

  public void empty(int anzahl) throws DoxiException {
    List<Pid> pids;
    try {
      pids = this.pidQueueService.getFirstBlock(anzahl);
      if (pids.size() == 0) {
        return;
      }
    } catch (Exception e) {
      throw new DoxiException(e);
    }

    for (Pid pid : pids) {
      try {
        this.em.getTransaction().begin();
        this.gwdgClient.update(pid);
        this.pidQueueService.remove(pid.getPidID());
        this.em.getTransaction().commit();
      } catch (Exception e) {
        LOG.error("ERROR: " + e);
        if (this.em.getTransaction().isActive()) {
          this.em.getTransaction().rollback();
        }
      }
    }
  }
}
