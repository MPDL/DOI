package de.mpg.mpdl.doxi.pidcache;

import java.net.URI;
import java.util.List;

import jakarta.persistence.EntityManager;

import de.mpg.mpdl.doxi.pidcache.model.PidID;

public class PidQueueDAO {
  private final EntityManager em;

  public PidQueueDAO(EntityManager em) {
    this.em = em;
  }

  public void create(PidQueue pidQueue) {
    this.em.persist(pidQueue);
  }
  
  public PidQueue find(PidID pidID) {
    return this.em.find(PidQueue.class, pidID.getIdAsString());
  }
  
  public List<PidQueue> findByUrl(URI url) {
    final String query = "SELECT q FROM pid_queue q where q.url= ?1";
    
    return this.em.createQuery(query, PidQueue.class).setParameter(1, url.toString()).getResultList();
  }
  
  public List<PidQueue> getFirst(int anz) {
    final String query = "SELECT q FROM pid_queue q ORDER BY q.created";
    
    return this.em.createQuery(query, PidQueue.class).setMaxResults(anz).getResultList();
  }
  
  public long getSize() {
    String query = "SELECT count(q) FROM pid_queue q";
    
    return this.em.createQuery(query, Long.class).getSingleResult();
  }
  
  public void remove(PidQueue pidQueue) {
    this.em.remove(pidQueue);
  }
}
