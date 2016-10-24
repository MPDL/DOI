package de.mpg.mpdl.doxi.pidcache;

import java.net.URI;
import java.util.Queue;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mpg.mpdl.doxi.exception.PidNotFoundException;
import de.mpg.mpdl.doxi.rest.JerseyApplicationConfig;

public class PidService implements PidCacheServiceInterface {
  private static final Logger LOG = LoggerFactory.getLogger(PidService.class);

  private final EntityManager em;
  private final PidCacheService pidCacheService;
  private final PidQueueService pidQueueService;
  private final GwdgClient gwdgClient;

  private int status = HttpServletResponse.SC_OK;
  private String location = "http://hdl.handle.net/XXX_LOCATION_XXX?noredirect";

  public PidService(GwdgClient gwdgController) {
    this.em = JerseyApplicationConfig.emf.createEntityManager();
    this.pidCacheService = new PidCacheService(this.em);
    this.pidQueueService = new PidQueueService(this.em);
    this.gwdgClient = gwdgController;
  }

  /**
   * This method does the following: - Take a PID from the cache - Change the URL of the PID - Put
   * the PID in the queue - Delete the PID from the cache - Return the PID
   * 
   * Notes: - The actual editing of the PID in the GWDG service will be proceed from the queue - The
   * cache will be completed by a new PID generated from {@link PidCacheProcess}
   **/
  @Override
  public String create(URI url) {
    PidID pidID = this.pidCacheService.getFirst();
    Pid pid = new Pid(pidID, url);
    this.pidQueueService.add(pid);
    this.pidCacheService.remove(pidID);
    this.status = HttpServletResponse.SC_CREATED;

    // TODO
    // return transformToPidServiceResponse(pid, "create");
    return null;
  }

  /**
   * Retrieve a PID from the GWDG PID service: - Check if PID still in queue, if yes, return it -
   * Check if GWDG PID service available, if no throw Exception
   * @throws PidNotFoundException 
   */
  @Override
  public String retrieve(PidID pidID) throws PidNotFoundException {
    Pid pid = this.pidQueueService.retrieve(pidID);
    if (pid != null) {
      // TODO
      // return transformToPidServiceResponse(pid, "view");
      return null;
    }

    // TODO
    return this.gwdgClient.retrieve(pidID).toString();
  }

  /**
   * Search a PID: - Search first in {@link Queue} if PID still in it - Check then if GWDG service
   * available - Search with GWDG service.
   * @throws PidNotFoundException 
   */
  @Override
  public String search(URI url) throws PidNotFoundException {
    Pid pid = this.pidQueueService.search(url);
    if (pid != null) {
      // TODO
      // return transformToPidServiceResponse(pid, "view");
      return null;
    }

    // TODO
    return this.gwdgClient.search(url).toString();
  }

  /**
   * Update a PID
   */
  @Override
  public String update(Pid pid) {
    this.pidQueueService.add(pid);
    this.status = HttpServletResponse.SC_OK;

    // TODO
    // return transformToPidServiceResponse(pid, "modify");
    return null;
  }

  /**
   * Should a PID be removable?
   */
  @Override
  public String delete(String id) {
    return "Delete not possible for a PID";
  }

  // private String transformToPidServiceResponse(Pid pid, String action) throws TechnicalException
  // {
  // this.location = this.location.replace("XXX_LOCATION_XXX", pid.getIdentifier());
  // PidServiceResponseVO pidServiceResponseVO = new PidServiceResponseVO();
  // pidServiceResponseVO.setAction(action);
  // pidServiceResponseVO.setCreator(GwdgClient.GWDG_PIDSERVICE_USER);
  // pidServiceResponseVO.setIdentifier(pid.getIdentifier());
  // pidServiceResponseVO.setUrl(pid.getUrl());
  // pidServiceResponseVO.setUserUid("anonymous");
  // pidServiceResponseVO.setInstitute("institute");
  // pid.setContact("jon@doe.xx");
  // pidServiceResponseVO.setMessage("Web proxy view URL: " + this.location);
  // return xmlTransforming.transformToPidServiceResponse(pidServiceResponseVO);
  // }

  @Override
  public int getCacheSize() {
    return this.pidCacheService.size();
  }

  @Override
  public int getQueueSize() {
    return this.pidQueueService.size();
  }

  @Override
  public String getLocation() {
    return this.location;
  }

  @Override
  public int getStatus() {
    return this.status;
  }
}
