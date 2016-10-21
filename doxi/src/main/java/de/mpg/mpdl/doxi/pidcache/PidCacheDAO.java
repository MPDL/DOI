package de.mpg.mpdl.doxi.pidcache;

import javax.persistence.EntityManager;

import de.mpg.mpdl.doxi.model.PidID;

public class PidCacheDAO {
  private final EntityManager em;

  public PidCacheDAO(EntityManager em) {
    this.em = em;
  }

  public void create(PidCache pidCache) {
    this.em.persist(pidCache);
  }
  
  public void remove(PidCache pidCache) {
    this.em.remove(pidCache);
  }
  
  public PidCache find(PidID pidID) {
    return this.em.find(PidCache.class, pidID.getIdAsString());
  }
  
  public PidCache getFirst() {
    final String query = "SELECT p FROM pid_cache p ORDER BY p.created DESC";
    return this.em.createQuery(query, PidCache.class).getSingleResult();
  }
  
  public int getSize() {
    final String query = "SELECT count(*) FROM pid_cache";
    return this.em.createQuery(query, Integer.class).getSingleResult();
  }
}
