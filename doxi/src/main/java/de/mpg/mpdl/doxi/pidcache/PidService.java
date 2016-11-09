package de.mpg.mpdl.doxi.pidcache;

import java.net.URI;

import javax.persistence.EntityManager;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import org.jibx.runtime.JiBXException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mpg.mpdl.doxi.exception.DoxiException;
import de.mpg.mpdl.doxi.exception.PidNotFoundException;
import de.mpg.mpdl.doxi.rest.EMF;

public class PidService implements PidServiceInterface {
  private static final Logger LOG = LoggerFactory.getLogger(PidService.class);

  private final GwdgClient gwdgClient;
  private final XMLTransforming xmlTransforming;

  @Context
  private SecurityContext secContext;

  public PidService() {
    this.gwdgClient = new GwdgClient();
    this.xmlTransforming = new XMLTransforming();
  }

  @Override
  public String create(URI url) throws DoxiException {
    LOG.info("User {} requests CREATE with URL {}", secContext.getUserPrincipal(), url);

    final EntityManager em = EMF.emf.createEntityManager();
    final PidCacheService pidCacheService = new PidCacheService(em);
    final PidQueueService pidQueueService = new PidQueueService(em);

    try {

      Pid _pid = pidQueueService.search(url);
      if (_pid != null) {
        throw new DoxiException("URL " + url + " already exists.");
      }

      if (this.gwdgClient.serviceAvailable()) {
        try {
          _pid = this.gwdgClient.search(url);
        } catch (PidNotFoundException e) {
          LOG.info("PID zur URL {} not found.", url);
        }

        if (_pid != null) {
          throw new DoxiException("URL " + url + " already exists.");
        }
      }

      PidID pidID = pidCacheService.getFirst();

      if (null == pidID) {
        throw new DoxiException("PidCache is empty.");
      }

      Pid pid = new Pid(pidID, url);

      em.getTransaction().begin();
      pidQueueService.add(pid);
      pidCacheService.remove(pidID);
      em.getTransaction().commit();

      return transformToPidServiceResponse(pid, "create");

    } catch (DoxiException e) {
      throw e;
    } catch (Exception e) {
      LOG.error("CREATE: URL {}:\n{}", url, e);
      if (em.getTransaction().isActive()) {
        em.getTransaction().rollback();
      }
      throw new DoxiException(e);
    } finally {
      if (em.isOpen()) {
        em.close();
      }
    }
  }

  @Override
  public String retrieve(PidID pidID) throws DoxiException {
    LOG.info("User {} requests RETRIEVE with ID {}", secContext.getUserPrincipal(), pidID);

    final EntityManager em = EMF.emf.createEntityManager();
    final PidQueueService pidQueueService = new PidQueueService(em);

    try {

      PidQueue pidQueue = pidQueueService.retrieve(pidID);

      if (pidQueue != null) {
        Pid pid = new Pid(pidID, pidQueue.getUrl());
        return transformToPidServiceResponse(pid, "view");
      }

      Pid pid = this.gwdgClient.retrieve(pidID);

      return transformToPidServiceResponse(pid, "view");

    } catch (Exception e) {
      LOG.error("RETRIEVE: ID {}:\n{}", pidID, e);
      throw new DoxiException(e);
    } finally {
      if (em.isOpen()) {
        em.close();
      }
    }
  }

  @Override
  public String search(URI url) throws DoxiException {
    LOG.info("User {} requests SEARCH with URL {}", secContext.getUserPrincipal(), url);

    final EntityManager em = EMF.emf.createEntityManager();
    final PidQueueService pidQueueService = new PidQueueService(em);

    try {

      Pid pid = pidQueueService.search(url);

      if (pid != null) {
        return transformToPidServiceResponse(pid, "search");
      }

      pid = this.gwdgClient.search(url);

      return transformToPidServiceResponse(pid, "search");

    } catch (Exception e) {
      LOG.error("SEARCH: URL {}:\n{}", url, e);
      throw new DoxiException(e);
    } finally {
      if (em.isOpen()) {
        em.close();
      }
    }
  }

  @Override
  public String update(Pid pid) throws DoxiException {
    LOG.info("User {} requests UPDATE with PID {}", secContext.getUserPrincipal(), pid);

    final EntityManager em = EMF.emf.createEntityManager();
    final PidQueueService pidQueueService = new PidQueueService(em);

    try {

      PidQueue pidQueue = pidQueueService.retrieve(pid.getPidID());

      if (pidQueue != null) {
        em.getTransaction().begin();
        pidQueue.setUrl(pid.getUrl());
        em.getTransaction().commit();
        return transformToPidServiceResponse(pid, "modify");
      }

      if (this.gwdgClient.serviceAvailable()) {
        try {
          this.gwdgClient.retrieve(pid.getPidID());
        } catch (PidNotFoundException e) {
          throw new DoxiException("PID does not exist.");
        }
      }

      em.getTransaction().begin();
      pidQueueService.add(pid);
      em.getTransaction().commit();

      return transformToPidServiceResponse(pid, "modify");

    } catch (DoxiException e) {
      throw e;
    } catch (Exception e) {
      LOG.error("UPDATE: PID {}:\n{}", pid, e);
      if (em.getTransaction().isActive()) {
        em.getTransaction().rollback();
      }
      throw new DoxiException(e);
    } finally {
      if (em.isOpen()) {
        em.close();
      }
    }
  }

  @Override
  public long getCacheSize() throws DoxiException {
    LOG.info("User {} requests CACHE_SIZE", secContext.getUserPrincipal());

    final EntityManager em = EMF.emf.createEntityManager();
    final PidCacheService pidCacheService = new PidCacheService(em);

    try {
      return pidCacheService.getSize();
    } catch (Exception e) {
      LOG.error("CACHE_SIZE:\n{}", e);
      throw new DoxiException(e);
    } finally {
      if (em.isOpen()) {
        em.close();
      }
    }
  }

  @Override
  public long getQueueSize() throws DoxiException {
    LOG.info("User {} requests QUEUE_SIZE", secContext.getUserPrincipal());

    final EntityManager em = EMF.emf.createEntityManager();
    final PidQueueService pidQueueService = new PidQueueService(em);

    try {
      return pidQueueService.getSize();
    } catch (Exception e) {
      LOG.error("QUEUE_SIZE:\n{}", e);
      throw new DoxiException(e);
    } finally {
      if (em.isOpen()) {
        em.close();
      }
    }
  }

  private String transformToPidServiceResponse(Pid pid, String action) throws JiBXException {
    final PidServiceResponseVO pidServiceResponseVO = new PidServiceResponseVO();
    pidServiceResponseVO.setAction(action);
    pidServiceResponseVO.setCreator(this.gwdgClient.getGwdgUser());
    pidServiceResponseVO.setIdentifier(pid.getPidID().getIdAsString());
    pidServiceResponseVO.setUrl(pid.getUrl().toString());
    pidServiceResponseVO.setUserUid("dummyUser");
    pidServiceResponseVO.setInstitute("dummyInstitute");
    pidServiceResponseVO.setContact("dummyContact");
    pidServiceResponseVO.setMessage("dummyMessage");
    pidServiceResponseVO.setMatches("dummyMatches");

    return xmlTransforming.transformToXML(pidServiceResponseVO);
  }
}
