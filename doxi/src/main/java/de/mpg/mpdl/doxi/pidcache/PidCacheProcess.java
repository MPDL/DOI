package de.mpg.mpdl.doxi.pidcache;

import java.net.URI;
import java.util.Date;

import jakarta.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mpg.mpdl.doxi.pidcache.model.Pid;
import de.mpg.mpdl.doxi.util.PropertyReader;

public class PidCacheProcess {
  private static final Logger LOG = LoggerFactory.getLogger(PidCacheProcess.class);

  private final GwdgClient gwdgClient;
  private final EntityManager em;
  private final PidCacheService pidCacheService;
  private final String dummyUrl;

  public PidCacheProcess(GwdgClient gwdgClient, EntityManager em) {
    this.gwdgClient = gwdgClient;
    this.em = em;
    this.pidCacheService = new PidCacheService(em);
    this.dummyUrl = PropertyReader.getProperty(PropertyReader.DOXI_PID_CACHE_DUMMY_URL);
  }

  public void fill(int anzahl) {
    final long timeStart = System.currentTimeMillis();;
    try {
      LOG.info("*** CACHE *** PidCacheProcess.fill() gestartet. Anzahl = {}", anzahl);
      if (this.pidCacheService.isFull()) {
        LOG.info("*** CACHE *** Cache is full.");
        return;
      }
      
      long current = 0;
      int i = 0;
      while (this.pidCacheService.isFull() == false && current != new Date().getTime() && i < anzahl) {
        current = new Date().getTime();
        try {
          this.em.getTransaction().begin();
          final Pid pid = gwdgClient.create(URI.create(this.dummyUrl.concat(Long.toString(current))));
          this.pidCacheService.add(pid.getPidID());
          this.em.getTransaction().commit();
        } catch (Exception e) {
          LOG.error("FILL:\n{}", e);
          if (this.em.getTransaction().isActive()) {
            this.em.getTransaction().rollback();
          }
        }
        i++;
      }
      LOG.info("*** CACHE ***  {} entries filled", i);
    } catch (Exception e) {
      LOG.error("FILL:\n{}", e);
    } finally {
      final long timeEnd = System.currentTimeMillis();
      LOG.info("*** CACHE *** PidCacheProcess.fill() beendet. Dauer: {} ms", (timeEnd - timeStart));
    }
  }
}
