package de.mpg.mpdl.doxi.controller;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.filter.LoggingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mpg.mpdl.doxi.exception.DoxiException;
import de.mpg.mpdl.doxi.exception.PidNotFoundException;
import de.mpg.mpdl.doxi.model.Pid;
import de.mpg.mpdl.doxi.model.PidID;
import de.mpg.mpdl.doxi.util.PropertyReader;

public class GwdgController implements GwdgControllerInterface {
  private static final Logger LOG = LoggerFactory.getLogger(GwdgController.class);
  
  private final String gwdgPidServiceCreate;
  private final String gwdgPidServiceView;
  private final String gwdgPidServiceSearch;
  private final String gwdgPidServiceUpdate;
  private final String gwdgPidServiceDelete;
  
  private final WebTarget gwdgTarget;

  @Context
  private SecurityContext secContext;

  public GwdgController() {
    ClientConfig clientConfig = new ClientConfig();
    
    HttpAuthenticationFeature authFeature = HttpAuthenticationFeature.basic( //
            PropertyReader.getProperty(PropertyReader.DOXI_PID_GWDG_USER_LOGIN),
            PropertyReader.getProperty(PropertyReader.DOXI_PID_GWDG_USER_PASSWORD));
    clientConfig.register(authFeature);
    
    Client client = ClientBuilder.newClient(clientConfig);
    client.register(new LoggingFilter(
        java.util.logging.Logger.getLogger("de.mpg.mpdl.doxi.controller.GwdgAPIController"),
        true));
    
    this.gwdgTarget = client.target(PropertyReader.getProperty(PropertyReader.DOXI_PID_GWDG_SERVICE_URL));
    
    this.gwdgPidServiceCreate = PropertyReader.getProperty(PropertyReader.DOXI_PID_GWDG_SERVICE_CREATE_PATH);
    this.gwdgPidServiceView   = PropertyReader.getProperty(PropertyReader.DOXI_PID_GWDG_SERVICE_VIEW_PATH);
    this.gwdgPidServiceSearch = PropertyReader.getProperty(PropertyReader.DOXI_PID_GWDG_SERVICE_SEARCH_PATH);
    this.gwdgPidServiceUpdate = PropertyReader.getProperty(PropertyReader.DOXI_PID_GWDG_SERVICE_UPDATE_PATH);
    this.gwdgPidServiceDelete = PropertyReader.getProperty(PropertyReader.DOXI_PID_GWDG_SERVICE_DELETE_PATH);
  }

  @Override
  public Pid create(URI url) throws DoxiException {
    LOG.info("User " + secContext.getUserPrincipal() + " requests create with url " + url);

    final Response response = gwdgTarget.path(this.gwdgPidServiceCreate).request(MediaType.TEXT_PLAIN_TYPE).post(Entity.text(url));
    if (response.getStatus() == Response.Status.OK.getStatusCode()) {
        Pid pid = new Pid(PidID.create(response.readEntity(String.class)), url); 
        LOG.info("create successfully returned pid " + pid);
        return pid;
    } else {
      LOG.error("Error create");
      throw new DoxiException(response.getStatus(), response.readEntity(String.class));
    }
  }

  @Override
  public Pid retrieve(PidID pidID) throws PidNotFoundException  {
    LOG.info("User " + secContext.getUserPrincipal() + " requests retrieve with ID " + pidID);

    final Response response = gwdgTarget.path(this.gwdgPidServiceView).path(pidID.getIdAsString()).request().get();
    if (response.getStatus() == Response.Status.OK.getStatusCode()) {
        Pid pid = new Pid(pidID, URI.create(response.readEntity(String.class))); 
        LOG.info("retrieve successfully returned pid " + pid);
        return pid;
    } else {
      LOG.error("Error retrieve");
      throw new PidNotFoundException(response.getStatus(), response.readEntity(String.class));
    }
  }

  @Override
  public Pid search(URI url) throws PidNotFoundException  {
    LOG.info("User " + secContext.getUserPrincipal() + " requests search with url " + url);

    final Response response = gwdgTarget.path(this.gwdgPidServiceSearch).path(url.toString()).request().get();
    if (response.getStatus() == Response.Status.OK.getStatusCode()) {
        Pid pid = new Pid(PidID.create(response.readEntity(String.class)), url); 
        LOG.info("search successfully returned pid " + pid);
        return pid;
    } else {
      LOG.error("Error search");
      throw new PidNotFoundException(response.getStatus(), response.readEntity(String.class));
    }
  }

  @Override
  public Pid update(Pid pid) throws PidNotFoundException {
    LOG.info("User " + secContext.getUserPrincipal() + " requests update with pid " + pid);

    final Response response = gwdgTarget.path(this.gwdgPidServiceUpdate).path(pid.getPidID().getIdAsString()).request().get();
    if (response.getStatus() == Response.Status.OK.getStatusCode()) {
        Pid _pid = new Pid(pid.getPidID(), URI.create(response.readEntity(String.class))); 
        LOG.info("retrieve successfully returned pid " + _pid);
        return _pid;
    } else {
      LOG.error("Error update");
      throw new PidNotFoundException(response.getStatus(), response.readEntity(String.class));
    }
  }

  @Override
  public void delete(PidID pidID) throws DoxiException  {
    LOG.info("User " + secContext.getUserPrincipal() + " requests delete() with identifier " + pidID);
    
    final Response response = gwdgTarget.path(this.gwdgPidServiceDelete).path(pidID.getIdAsString()).request().delete();
    if (response.getStatus() == Response.Status.OK.getStatusCode()) {
      LOG.info("delete successfully deleted pid " + pidID);
    } else {
      LOG.error("Error delete");
      throw new DoxiException(response.getStatus(), response.readEntity(String.class));
    }
  }

  @Override
  public boolean serviceAvailable() {
    LOG.info("User " + secContext.getUserPrincipal() + " requests serviceAvailable() + ");

    final Response response = gwdgTarget.request().get();
    if (response.getStatus() == Response.Status.OK.getStatusCode()) {
      LOG.info("serviceAvailable() successfully");
        return true;
    } else {
      LOG.error("Error: Service not available");
      return false;
    }
  }
}
