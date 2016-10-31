package de.mpg.mpdl.doxi.pidcache;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.filter.LoggingFilter;
import org.jibx.runtime.JiBXException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mpg.mpdl.doxi.exception.GwdgException;
import de.mpg.mpdl.doxi.exception.PidNotFoundException;
import de.mpg.mpdl.doxi.util.PropertyReader;

public class GwdgClient {
  private static final Logger LOG = LoggerFactory.getLogger(GwdgClient.class);

  private static final String URL = "url";
  private static final String PID = "pid";

  private final String gwdgPidServiceCreate;
  private final String gwdgPidServiceView;
  private final String gwdgPidServiceSearch;
  private final String gwdgPidServiceUpdate;

  private final WebTarget gwdgTarget;
  private final XMLTransforming xmlTransforming;

  private final String gwdgUser;

  public GwdgClient() {
    ClientConfig clientConfig = new ClientConfig();

    this.gwdgUser = PropertyReader.getProperty(PropertyReader.DOXI_PID_GWDG_USER_LOGIN);
    HttpAuthenticationFeature authFeature = HttpAuthenticationFeature.basic( //
        this.gwdgUser, PropertyReader.getProperty(PropertyReader.DOXI_PID_GWDG_USER_PASSWORD));
    clientConfig.register(authFeature);

    Client client = ClientBuilder.newClient(clientConfig);

    final int timeout =
        Integer.parseInt(PropertyReader.getProperty(PropertyReader.DOXI_PID_GWDG_TIMEOUT));
    client.property(ClientProperties.CONNECT_TIMEOUT, timeout);
    client.property(ClientProperties.READ_TIMEOUT, timeout);

    client.register(new LoggingFilter(java.util.logging.Logger.getLogger("GwdgJersey"), true));

    this.gwdgTarget =
        client.target(PropertyReader.getProperty(PropertyReader.DOXI_PID_GWDG_SERVICE_URL));
    this.gwdgPidServiceCreate =
        PropertyReader.getProperty(PropertyReader.DOXI_PID_GWDG_SERVICE_CREATE_PATH);
    this.gwdgPidServiceView =
        PropertyReader.getProperty(PropertyReader.DOXI_PID_GWDG_SERVICE_VIEW_PATH);
    this.gwdgPidServiceSearch =
        PropertyReader.getProperty(PropertyReader.DOXI_PID_GWDG_SERVICE_SEARCH_PATH);
    this.gwdgPidServiceUpdate =
        PropertyReader.getProperty(PropertyReader.DOXI_PID_GWDG_SERVICE_UPDATE_PATH);

    this.xmlTransforming = new XMLTransforming();
  }

  public Pid create(URI url) throws GwdgException, JiBXException {
    LOG.info("User requests CREATE with URL {}", url);

    try {

      Response response = gwdgTarget //
          .path(this.gwdgPidServiceCreate) //
          .queryParam(URL, url.toString()).request(MediaType.TEXT_PLAIN_TYPE).post(null);

      if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
        String xml = response.readEntity(String.class);
        PidServiceResponseVO vo = xmlTransforming.transformToVO(xml);
        Pid pid = new Pid(PidID.create(vo.getIdentifier()), URI.create(vo.getUrl()));
        LOG.info("create successfully returned pid {}", pid);
        return pid;
      }

      throw new GwdgException(response.getStatus(), response.readEntity(String.class));

    } catch (GwdgException | JiBXException e) {
      LOG.error("CREATE: URL {}:\n{}", url, e);
      throw e;
    }
  }

  public Pid retrieve(PidID pidID) throws PidNotFoundException, GwdgException, JiBXException {
    LOG.info("User requests RETRIEVE with ID {}", pidID);

    try {

      final Response response = this.gwdgTarget //
          .path(this.gwdgPidServiceView) //
          .queryParam(PID, pidID.getIdAsString()).request(MediaType.TEXT_PLAIN_TYPE).get();

      if (response.getStatus() == Response.Status.OK.getStatusCode()) {
        String xml = response.readEntity(String.class);
        PidServiceResponseVO vo = xmlTransforming.transformToVO(xml);
        Pid pid = new Pid(PidID.create(vo.getIdentifier()), URI.create(vo.getUrl()));
        LOG.info("retrieve successfully returned pid {}", pid);
        return pid;
      }

      if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
        throw new PidNotFoundException(response.getStatus(), response.readEntity(String.class));
      }

      throw new GwdgException(response.getStatus(), response.readEntity(String.class));

    } catch (PidNotFoundException e) {
      throw e;
    } catch (GwdgException | JiBXException e) {
      LOG.error("RETRIEVE: ID {}:\n{}", pidID, e);
      throw e;
    }
  }

  public Pid search(URI url) throws PidNotFoundException, GwdgException, JiBXException {
    LOG.info("User requests SEARCH with URL {}", url);

    Response response;
    try {

      response = this.gwdgTarget //
          .path(this.gwdgPidServiceSearch) //
          .queryParam(URL, url.toString()).request(MediaType.TEXT_PLAIN_TYPE).get();

      if (response.getStatus() == Response.Status.OK.getStatusCode()) {
        String xml = response.readEntity(String.class);
        PidServiceResponseVO vo = xmlTransforming.transformToVO(xml);
        Pid pid = new Pid(PidID.create(vo.getIdentifier()), URI.create(vo.getUrl()));
        LOG.info("search successfully returned pid " + pid);
        return pid;
      }

      if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
        throw new PidNotFoundException(response.getStatus(), response.readEntity(String.class));
      }

      throw new GwdgException(response.getStatus(), response.readEntity(String.class));

    } catch (PidNotFoundException e) {
      throw e;
    } catch (GwdgException | JiBXException e) {
      LOG.error("SEARCH: URL {}:\n{}", url, e);
      throw e;
    }
  }

  public Pid update(Pid pid) throws PidNotFoundException, GwdgException, JiBXException {
    LOG.info("User requests UPDATE with PID {}", pid);

    try {

      try {
        this.retrieve(pid.getPidID());
      } catch (PidNotFoundException e) {
        throw e;
      }

      Form form = new Form();
      form.param(URL, pid.getUrl().toString());

      final Response response = gwdgTarget //
          .path(this.gwdgPidServiceUpdate) //
          .queryParam(PID, pid.getPidID().getIdAsString()).request(MediaType.TEXT_PLAIN_TYPE)
          .post(Entity.form(form));

      if (response.getStatus() == Response.Status.OK.getStatusCode()) {
        String xml = response.readEntity(String.class);
        PidServiceResponseVO vo = xmlTransforming.transformToVO(xml);
        Pid _pid = new Pid(PidID.create(vo.getIdentifier()), URI.create(vo.getUrl()));
        LOG.info("update successfully returned pid {}", _pid);
        return _pid;
      }

      throw new GwdgException(response.getStatus(), response.readEntity(String.class));

    } catch (PidNotFoundException e) {
      throw e;
    } catch (GwdgException | JiBXException e) {
      LOG.error("UPDATE: PID {}:\n{}", pid, e);
      throw e;
    }
  }

  public boolean serviceAvailable() {
    LOG.info("User requests SERVICE_AVAILABLE");

    try {

      final Response response = this.gwdgTarget.request().get();

      if (response.getStatus() == Response.Status.OK.getStatusCode()) {
        LOG.info("Service available");
        return true;
      }

      throw new GwdgException(response.getStatus(), response.readEntity(String.class));

    } catch (GwdgException e) {
      LOG.warn("SERVICE not available:\n{}", e);
      return false;
    }
  }

  public String getGwdgUser() {
    return this.gwdgUser;
  }
}
