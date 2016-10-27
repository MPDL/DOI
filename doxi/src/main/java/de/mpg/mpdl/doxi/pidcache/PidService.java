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
import de.mpg.mpdl.doxi.rest.JerseyApplicationConfig;

public class PidService implements PidServiceInterface {
  private static final Logger LOG = LoggerFactory.getLogger(PidService.class);

  private final EntityManager em;
  private final PidCacheService pidCacheService;
  private final PidQueueService pidQueueService;
  private final GwdgClient gwdgClient;
  private final XMLTransforming xmlTransforming;

  @Context
  private SecurityContext secContext;

  public PidService() {
    this.em = JerseyApplicationConfig.emf.createEntityManager();
    this.pidCacheService = new PidCacheService(this.em);
    this.pidQueueService = new PidQueueService(this.em);
    this.gwdgClient = new GwdgClient();
    this.xmlTransforming = new XMLTransforming();
  }

  @Override
  public String create(URI url) throws DoxiException {
    // TODO
    if (secContext != null) {
      LOG.info("User " + secContext.getUserPrincipal() + " requests create with url " + url);
    } else {
      LOG.info("User requests create with url " + url);
    }

    try {
      Pid _pid = this.pidQueueService.search(url);
      if (_pid != null) {
        throw new DoxiException("URL already exists.");
      }

      try {
        _pid = this.gwdgClient.search(url);
      } catch (PidNotFoundException e) {
      }

      if (_pid != null) {
        throw new DoxiException("URL already exists.");
      }

      PidID pidID = this.pidCacheService.getFirst();

      if (null == pidID) {
        throw new DoxiException("PidCache is empty.");
      }

      Pid pid = new Pid(pidID, url);

      this.em.getTransaction().begin();
      this.pidQueueService.add(pid);
      this.pidCacheService.remove(pidID);
      this.em.getTransaction().commit();

      return transformToPidServiceResponse(pid, "create");
    } catch (DoxiException e) {
      throw e;
    } catch (Exception e) {
      LOG.error("create: url " + url + ": "+ e);
      if (this.em.getTransaction().isActive()) {
        this.em.getTransaction().rollback();
      }
      throw new DoxiException(e);
    }
  }

  @Override
  public String retrieve(PidID pidID) throws DoxiException {
    try {
      Pid pid = this.pidQueueService.retrieve(pidID);

      if (pid != null) {
        return transformToPidServiceResponse(pid, "view");
      }

      pid = this.gwdgClient.retrieve(pidID);

      return transformToPidServiceResponse(pid, "view");
    } catch (Exception e) {
      LOG.error("retrieve: pidID " + pidID + ": "+ e);
      throw new DoxiException(e);
    }
  }

  @Override
  public String search(URI url) throws DoxiException {
    try {
      Pid pid = this.pidQueueService.search(url);

      if (pid != null) {
        return transformToPidServiceResponse(pid, "search");
      }

      pid = this.gwdgClient.search(url);

      return transformToPidServiceResponse(pid, "search");
    } catch (Exception e) {
      LOG.error("search: url " + url + ": "+ e);
      throw new DoxiException(e);
    }
  }

  @Override
  public String update(Pid pid) throws DoxiException {
    try {
      Pid _pid = this.pidQueueService.retrieve(pid.getPidID());

      if (_pid != null) {
        this.em.getTransaction().begin();
        this.pidQueueService.update(pid);
        this.em.getTransaction().commit();
        return transformToPidServiceResponse(pid, "modify");
      }

      try {
        this.gwdgClient.retrieve(pid.getPidID());
      } catch (PidNotFoundException e) {
        throw new DoxiException("PID does not exist.");
      }

      this.em.getTransaction().begin();
      this.pidQueueService.add(pid);
      this.em.getTransaction().commit();

      return transformToPidServiceResponse(pid, "modify");
    } catch (DoxiException e) {
      throw e;
    } catch (Exception e) {
      LOG.error("update: pid " + pid + ": "+ e);
      if (this.em.getTransaction().isActive()) {
        this.em.getTransaction().rollback();
      }
      throw new DoxiException(e);
    }
  }

  @Override
  public long getCacheSize() throws DoxiException {
    try {
      return this.pidCacheService.getSize();
    } catch (Exception e) {
      LOG.error("getCacheSize: " + e);
      throw new DoxiException(e);
    }
  }

  @Override
  public long getQueueSize() throws DoxiException {
    try {
      return this.pidQueueService.getSize();
    } catch (Exception e) {
      LOG.error("getQueueSize: " + e);
      throw new DoxiException(e);
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
