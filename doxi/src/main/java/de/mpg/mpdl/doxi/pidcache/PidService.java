package de.mpg.mpdl.doxi.pidcache;

import java.net.URI;
import java.util.Queue;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mpg.mpdl.doxi.exception.DoxiException;
import de.mpg.mpdl.doxi.rest.JerseyApplicationConfig;

public class PidService implements PidServiceInterface {
  private static final Logger LOG = LoggerFactory.getLogger(PidService.class);

  private final EntityManager em;
  private final PidCacheService pidCacheService;
  private final PidQueueService pidQueueService;
  private final GwdgClient gwdgClient;
  private final XMLTransforming xmlTransforming;

  public PidService() {
    this.em = JerseyApplicationConfig.emf.createEntityManager();
    this.pidCacheService = new PidCacheService(this.em);
    this.pidQueueService = new PidQueueService(this.em);
    this.gwdgClient = new GwdgClient();
    this.xmlTransforming = new XMLTransforming();
  }

  /**
   * This method does the following: - Take a PID from the cache - Change the URL of the PID - Put
   * the PID in the queue - Delete the PID from the cache - Return the PID
   * 
   * Notes: - The actual editing of the PID in the GWDG service will be proceed from the queue - The
   * cache will be completed by a new PID generated from {@link PidCacheProcess}
   * 
   * @throws DoxiException
   **/
  @Override
  public String create(URI url) throws DoxiException {
    try {
      // TODO Cache gefüllt?
      PidID pidID = this.pidCacheService.getFirst();
      Pid pid = new Pid(pidID, url);

      this.em.getTransaction().begin();
      this.pidQueueService.add(pid);
      this.pidCacheService.remove(pidID);
      this.em.getTransaction().commit();

      return transformToPidServiceResponse(pid, "create");
    } catch (PidCacheServiceException | PidQueueServiceException e) {
      if (this.em.getTransaction().isActive()) {
        this.em.getTransaction().rollback();
      }
      throw new DoxiException(e);
    }
  }

  /**
   * Retrieve a PID from the GWDG PID service: - Check if PID still in queue, if yes, return it -
   * Check if GWDG PID service available, if no throw Exception
   * 
   * @throws DoxiException
   */
  @Override
  public String retrieve(PidID pidID) throws DoxiException {
    try {
      Pid pid = this.pidQueueService.retrieve(pidID);
      if (pid != null) {
        return transformToPidServiceResponse(pid, "view");
      }
      return this.gwdgClient.retrieve(pidID).toString();
    } catch (PidQueueServiceException | GwdgException e) {
      throw new DoxiException(e);
    }
  }

  /**
   * Search a PID: - Search first in {@link Queue} if PID still in it - Check then if GWDG service
   * available - Search with GWDG service.
   * 
   * @throws DoxiException
   */
  @Override
  public String search(URI url) throws DoxiException {
    try {
      Pid pid = this.pidQueueService.search(url);
      if (pid != null) {
        return transformToPidServiceResponse(pid, "view");
      }
      return this.gwdgClient.search(url).toString();
    } catch (PidQueueServiceException | GwdgException e) {
      throw new DoxiException(e);
    }
  }

  /**
   * Update a PID
   */
  @Override
  public String update(Pid pid) throws DoxiException {
    try {
      this.em.getTransaction().begin();
      this.pidQueueService.add(pid);
      this.em.getTransaction().commit();

      return transformToPidServiceResponse(pid, "modify");
    } catch (PidQueueServiceException e) {
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
    } catch (PidCacheServiceException e) {
      throw new DoxiException(e);
    }
  }

  @Override
  public long getQueueSize() throws DoxiException {
    try {
      return this.pidQueueService.getSize();
    } catch (PidQueueServiceException e) {
      throw new DoxiException(e);
    }
  }

  private String transformToPidServiceResponse(Pid pid, String action) {
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
