package de.mpg.mpdl.doxi.pidcache;

import java.util.List;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mpg.mpdl.doxi.exception.DoxiException;
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

  /*
   * PidQueue wird geleert und die enthaltenen PIDs auf GWDG Seite geupdatet.
   * Das geht aber nur, wenn die PID auf GWDG Seite bereits bekannt ist, und die neue URL dort noch nicht vergeben wurde.
   */
  public void empty(int anzahl) throws DoxiException {
    try {
      
      List<Pid> pids = this.pidQueueService.getFirstBlock(anzahl);
      
      if (pids.size() == 0) {
        return;
      }

      for (Pid pid : pids) {
        try { // Pruefung: URL bei GWDG bereits vorhanden
          this.gwdgClient.search(pid.getUrl());
          LOG.error("URL already exists. Could not update PID: {}", pid);
        } catch (PidNotFoundException e1) {
          try { // Pruefung: PID bei GWDG ueberhaupt vorhanden
            this.gwdgClient.retrieve(pid.getPidID());
            this.gwdgClient.update(pid);
          } catch (PidNotFoundException e2) {
            LOG.error("PID does not exists. Could not update PID: {}", pid);
          }
        }

        // in jedem Fall PID aus Queue entfernen
        this.em.getTransaction().begin();
        this.pidQueueService.remove(pid.getPidID());
        this.em.getTransaction().commit();
        LOG.info("{} entries done", pids.size());
      }
      
    } catch (Exception e) {
      LOG.error("EMPTY:\n{}", e);
      if (this.em.getTransaction().isActive()) {
        this.em.getTransaction().rollback();
      }
    }
  }
}
