package de.mpg.mpdl.doxi.pidcache;

import java.net.URI;
import java.util.Queue;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mpg.mpdl.doxi.exception.PidNotFoundException;

public class PidService implements PidCacheServiceInterface {
  private static final Logger LOG = LoggerFactory.getLogger(PidService.class);

  private final CacheManager cacheManager;
  private final QueueManager queueManager;
  private final GwdgClient gwdgController;

  private int status = HttpServletResponse.SC_OK;
  private String location = "http://hdl.handle.net/XXX_LOCATION_XXX?noredirect";

  public PidService(EntityManager em, GwdgClient gwdgController) {
    this.cacheManager = new CacheManager(em);
    this.queueManager = new QueueManager(em);
    this.gwdgController = gwdgController;
  }

  /**
   * This method does the following: - Take a PID from the cache - Change the URL of the PID - Put
   * the PID in the queue - Delete the PID from the cache - Return the PID
   * 
   * Notes: - The actual editing of the PID in the GWDG service will be proceed from the queue - The
   * cache will be completed by a new PID generated from {@link CacheProcess}
   **/
  @Override
  public String create(URI url) {
    PidID pidID = this.cacheManager.getFirst();
    Pid pid = new Pid(pidID, url);
    this.queueManager.add(pid);
    this.cacheManager.remove(pidID);
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
    Pid pid = this.queueManager.retrieve(pidID);
    if (pid != null) {
      // TODO
      // return transformToPidServiceResponse(pid, "view");
      return null;
    }

    // TODO
    return this.gwdgController.retrieve(pidID).toString();
  }

  /**
   * Search a PID: - Search first in {@link Queue} if PID still in it - Check then if GWDG service
   * available - Search with GWDG service.
   * @throws PidNotFoundException 
   */
  @Override
  public String search(URI url) throws PidNotFoundException {
    Pid pid = this.queueManager.search(url);
    if (pid != null) {
      // TODO
      // return transformToPidServiceResponse(pid, "view");
      return null;
    }

    // TODO
    return this.gwdgController.search(url).toString();
  }

  /**
   * Update a PID
   */
  @Override
  public String update(Pid pid) {
    this.queueManager.add(pid);
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
    return this.cacheManager.size();
  }

  @Override
  public int getQueueSize() {
    return this.queueManager.size();
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
