package de.mpg.mpdl.doxi.pidcache;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mpg.mpdl.doxi.exception.GwdgException;
import de.mpg.mpdl.doxi.exception.PidNotFoundException;
import de.mpg.mpdl.doxi.pidcache.json.EpicPid;
import de.mpg.mpdl.doxi.pidcache.json.FullPid;
import de.mpg.mpdl.doxi.pidcache.json.GwdgInput;
import de.mpg.mpdl.doxi.pidcache.model.Pid;
import de.mpg.mpdl.doxi.pidcache.model.PidID;
import de.mpg.mpdl.doxi.util.PropertyReader;

public class GwdgClient {
  private static final Logger LOG = LoggerFactory.getLogger(GwdgClient.class);

  private static final String URL = "URL";

  private final WebTarget gwdgTarget;
  private final String gwdgSuffix;
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

    client.register(new LoggingFeature(java.util.logging.Logger.getLogger(this.getClass().getCanonicalName())));
    //client.register(JacksonJaxbJsonProvider.class);

    this.gwdgTarget =
        client.target(PropertyReader.getProperty(PropertyReader.DOXI_PID_GWDG_SERVICE_URL));
    this.gwdgSuffix = PropertyReader.getProperty(PropertyReader.DOXI_PID_GWDG_SERVICE_SUFFIX);
  }

  public Pid create(URI url) throws GwdgException {
    LOG.info("User requests CREATE with URL {}", url);

    try {
      
      List<GwdgInput> list = new ArrayList<GwdgInput>();
      list.add(new GwdgInput(GwdgClient.URL, url.toString()));
      
      Response response = this.gwdgTarget //
          .path(this.gwdgSuffix) //
          .request(MediaType.APPLICATION_JSON) //
          .accept(MediaType.APPLICATION_JSON) //
          .post(Entity.json(list));

      if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
        EpicPid result = response.readEntity(EpicPid.class);
        Pid pid = new Pid(PidID.create(result.getEpicPid()), url);
        LOG.info("create successfully returned pid {}", pid);
        return pid;
      }

      throw new GwdgException(response.getStatus(), response.readEntity(String.class));

    } catch (GwdgException e) {
      LOG.error("CREATE: URL {}:\n{}", url, e);
      throw e;
    }
  }

  public Pid retrieve(PidID pidID) throws PidNotFoundException, GwdgException {
    LOG.info("User requests RETRIEVE with ID {}", pidID);

    try {

      final Response response = this.gwdgTarget //
          .path(pidID.getIdAsString()) //
          .request() //
          .accept(MediaType.APPLICATION_JSON) //
          .get();

      if (response.getStatus() == Response.Status.OK.getStatusCode()) {
        List<FullPid> result = response.readEntity(new GenericType<List<FullPid>>(){});
        
        LOG.info("create successfully returned pid {}", result);
        return new Pid(pidID, URI.create((String)result.get(0).getParsedData()));
      }

      if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
        throw new PidNotFoundException(response.getStatus(), response.readEntity(String.class));
      }

      throw new GwdgException(response.getStatus(), response.readEntity(String.class));

    } catch (PidNotFoundException e) {
      LOG.warn("RETRIEVE: ID {}:\n{}", pidID, e);
      throw e;
    } catch (GwdgException e) {
      LOG.error("RETRIEVE: ID {}:\n{}", pidID, e);
      throw e;
    }
  }

  public String search(URI url) throws PidNotFoundException, GwdgException {
    LOG.info("User requests SEARCH with URL {}", url);

    Response response;
    String result = null;
    try {

      response = this.gwdgTarget //
          .path(this.gwdgSuffix) //
          .queryParam(URL, (url.toString()))
          .request() //
          .accept(MediaType.APPLICATION_JSON) //
          .get();

      if (response.getStatus() == Response.Status.OK.getStatusCode()) {
        result = response.readEntity(String.class);
        
        if (result.isEmpty() || "[]".equals(result)) {
          throw new PidNotFoundException(response.getStatus(), result);
        }
        
        LOG.info("search successfully returned pids");
        return result;
      }

      if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
        throw new PidNotFoundException(response.getStatus(), result);
      }

      throw new GwdgException(response.getStatus(), result);

    } catch (PidNotFoundException e) {
      LOG.warn("SEARCH: URL {}:\n{}", url, e);
      throw e;
    } catch (GwdgException e) {
      LOG.error("SEARCH: URL {}:\n{}", url, e);
      throw e;
    }
  }

  public Pid update(Pid pid) throws PidNotFoundException, GwdgException {
    LOG.info("User requests UPDATE with PID {}", pid);

    try {

      try {
        this.retrieve(pid.getPidID());
      } catch (PidNotFoundException e) {
        throw e;
      }

      List<GwdgInput> list = new ArrayList<GwdgInput>();
      list.add(new GwdgInput(GwdgClient.URL, pid.getUrl().toString()));
      
      final Response response = this.gwdgTarget //
          .path(pid.getPidID().getIdAsString()) //
          .request(MediaType.APPLICATION_JSON) //
          .accept(MediaType.APPLICATION_JSON) //
          .put(Entity.json(list));

      if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()) {
        LOG.info("update successful for PID {}", pid);
        return pid;
      }

      throw new GwdgException(response.getStatus(), response.readEntity(String.class));

    } catch (PidNotFoundException e) {
      throw e;
    } catch (GwdgException e) {
      LOG.error("UPDATE: PID {}:\n{}", pid, e);
      throw e;
    }
  }

  public boolean serviceAvailable() {
    LOG.info("User requests SERVICE_AVAILABLE");

    try {

      final Response response = this.gwdgTarget //
          .path(this.gwdgSuffix) //
          .request() //
          .accept(MediaType.APPLICATION_JSON) //
          .get();

      if (response.getStatus() == Response.Status.OK.getStatusCode()) {
        LOG.info("Service available");
        return true;
      }

      throw new GwdgException(response.getStatus(), response.readEntity(String.class));

    } catch (Exception e) {
      LOG.warn("SERVICE not available:\n{}", e);
      return false;
    }
  }

  public String getGwdgUser() {
    return this.gwdgUser;
  }
}
