package de.mpg.mpdl.doxi.pidcache;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.filter.LoggingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mpg.mpdl.doxi.util.PropertyReader;

public class GwdgClient implements GwdgClientInterface {
  private static final Logger LOG = LoggerFactory.getLogger(GwdgClient.class);

  private static final String URL = "url";
  private static final String PID = "pid";
  
  private final String gwdgPidServiceCreate;
  private final String gwdgPidServiceView;
  private final String gwdgPidServiceSearch;
  private final String gwdgPidServiceUpdate;
  private final String gwdgPidServiceDelete;

  private final WebTarget gwdgTarget;
  private final XMLTransforming xmlTransforming;
  
  private final String gwdgUser;

  @Context
  private SecurityContext secContext;

  public GwdgClient() {
    ClientConfig clientConfig = new ClientConfig();

    this.gwdgUser = PropertyReader.getProperty(PropertyReader.DOXI_PID_GWDG_USER_LOGIN); 
    HttpAuthenticationFeature authFeature = HttpAuthenticationFeature.basic( //
        this.gwdgUser, PropertyReader.getProperty(PropertyReader.DOXI_PID_GWDG_USER_PASSWORD));
    clientConfig.register(authFeature);
    
    Client client = ClientBuilder.newClient(clientConfig);
    
    final int timeout = Integer.parseInt(PropertyReader.getProperty(PropertyReader.DOXI_PID_GWDG_TIMEOUT)); 
    client.property(ClientProperties.CONNECT_TIMEOUT, timeout);
    client.property(ClientProperties.READ_TIMEOUT, timeout);
    
    client.register(new LoggingFilter(
        java.util.logging.Logger.getLogger("de.mpg.mpdl.doxi.pidcache.GwdgClient"), true));

    this.gwdgTarget = client.target(PropertyReader.getProperty(PropertyReader.DOXI_PID_GWDG_SERVICE_URL));

    this.gwdgPidServiceCreate = PropertyReader.getProperty(PropertyReader.DOXI_PID_GWDG_SERVICE_CREATE_PATH);
    this.gwdgPidServiceView = PropertyReader.getProperty(PropertyReader.DOXI_PID_GWDG_SERVICE_VIEW_PATH);
    this.gwdgPidServiceSearch = PropertyReader.getProperty(PropertyReader.DOXI_PID_GWDG_SERVICE_SEARCH_PATH);
    this.gwdgPidServiceUpdate = PropertyReader.getProperty(PropertyReader.DOXI_PID_GWDG_SERVICE_UPDATE_PATH);
    this.gwdgPidServiceDelete = PropertyReader.getProperty(PropertyReader.DOXI_PID_GWDG_SERVICE_DELETE_PATH);

    this.xmlTransforming = new XMLTransforming();
  }

  @Override
  public Pid create(URI url) throws GwdgException {
    // TODO
    if (secContext != null) {
      LOG.info("User " + secContext.getUserPrincipal() + " requests create with url " + url);
    } else {
      LOG.info("User requests create with url " + url);
    }

    final Response response = gwdgTarget //
        .path(this.gwdgPidServiceCreate) //
        .queryParam(URL, url.toString())
        .request(MediaType.TEXT_PLAIN_TYPE).post(null); // leerer Body
    
    if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
      String xml = response.readEntity(String.class);
      PidServiceResponseVO vo = xmlTransforming.transformToVO(xml);
      Pid pid = new Pid(PidID.create(vo.getIdentifier()), URI.create(vo.getUrl()));
      LOG.info("create successfully returned pid " + pid);
      return pid;
    } else { // TODO
      LOG.error("Error create");
      throw new GwdgException(response.getStatus(), response.readEntity(String.class));
    }
  }

  @Override
  public Pid retrieve(PidID pidID) throws GwdgException {
    // TODO
    if (secContext != null) {
      LOG.info("User " + secContext.getUserPrincipal() + " requests retrieve with ID " + pidID);
    } else {
      LOG.info("User requests retrieve with ID " + pidID);
    }

    final Response response = this.gwdgTarget //
        .path(this.gwdgPidServiceView) //
        .queryParam(PID, pidID.getIdAsString())
        .request(MediaType.TEXT_PLAIN_TYPE).get();
    
    if (response.getStatus() == Response.Status.OK.getStatusCode()) {
      String xml = response.readEntity(String.class);
      PidServiceResponseVO vo = xmlTransforming.transformToVO(xml);
      Pid pid = new Pid(PidID.create(vo.getIdentifier()), URI.create(vo.getUrl()));
      LOG.info("retrieve successfully returned pid " + pid);
      return pid;
    } else { // TODO
      LOG.error("Error retrieve");
      throw new GwdgException(response.getStatus(), response.readEntity(String.class));
    }
  }

  @Override
  public Pid search(URI url) throws GwdgException {
    // TODO
    if (secContext != null) {
      LOG.info("User " + secContext.getUserPrincipal() + " requests search with url " + url);
    } else {
      LOG.info("User requests search with url " + url);
    }

    final Response response = this.gwdgTarget //
        .path(this.gwdgPidServiceSearch) //
        .queryParam(URL, url.toString())
        .request(MediaType.TEXT_PLAIN_TYPE).get();
    
    if (response.getStatus() == Response.Status.OK.getStatusCode()) {
      String xml = response.readEntity(String.class);
      PidServiceResponseVO vo = xmlTransforming.transformToVO(xml);
      Pid pid = new Pid(PidID.create(vo.getIdentifier()), URI.create(vo.getUrl()));
      LOG.info("search successfully returned pid " + pid);
      return pid;
    } else { // TODO
      LOG.error("Error search");
      throw new GwdgException(response.getStatus(), response.readEntity(String.class));
    }
  }

  @Override
  public Pid update(Pid pid) throws GwdgException {
    // TODO
    if (secContext != null) {
      LOG.info("User " + secContext.getUserPrincipal() + " requests update with pid " + pid);
    } else {
      LOG.info("User requests update with pid " + pid);
    }

    Form form = new Form();
    form.param(URL, pid.getUrl().toString());
    
    final Response response = gwdgTarget //
        .path(this.gwdgPidServiceUpdate) //
        .queryParam(PID, pid.getPidID().getIdAsString())
        .request(MediaType.TEXT_PLAIN_TYPE).post(Entity.form(form));
    
    if (response.getStatus() == Response.Status.OK.getStatusCode()) {
      String xml = response.readEntity(String.class);
      PidServiceResponseVO vo = xmlTransforming.transformToVO(xml);
      Pid _pid = new Pid(PidID.create(vo.getIdentifier()), URI.create(vo.getUrl()));
      LOG.info("update successfully returned pid " + _pid);
      return _pid;
    } else { // TODO
      LOG.error("Error update");
      throw new GwdgException(response.getStatus(), response.readEntity(String.class));
    }
  }

  @Override
  public Pid delete(PidID pidID) throws GwdgException {
    // TODO
    if (secContext != null) {
      LOG.info("User " + secContext.getUserPrincipal() + " requests delete() with identifier " + pidID);
    } else {
      LOG.info("User requests delete() with identifier " + pidID);
    }

    final Response response = this.gwdgTarget //
        .path(this.gwdgPidServiceDelete) //
        .queryParam(PID, pidID.getIdAsString())
        .request().delete();
    
    if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()) {
      String xml = response.readEntity(String.class);
      PidServiceResponseVO vo = xmlTransforming.transformToVO(xml);
      Pid _pid = new Pid(PidID.create(vo.getIdentifier()), URI.create(vo.getUrl()));
      LOG.info("delete successfully deleted pid " + _pid);
      return _pid;
    } else { // TODO
      LOG.error("Error delete");
      throw new GwdgException(response.getStatus(), response.readEntity(String.class));
    }
  }

  @Override
  public boolean serviceAvailable() {
    // TODO
    if (secContext != null) {
      LOG.info("User " + secContext.getUserPrincipal() + " requests serviceAvailable() + ");
    } else {
      LOG.info("User requests serviceAvailable() + ");
    }

    final Response response = this.gwdgTarget.request().get();
    
    if (response.getStatus() == Response.Status.OK.getStatusCode()) {
      LOG.info("serviceAvailable() successfully");
      return true;
    } else { // TODO
      LOG.error("Error: Service not available");
      return false;
    }
  }
  
  @Override
  public String getGwdgUser() {
    return this.gwdgUser;
  }
}
