package de.mpg.mpdl.doxi.view;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.glassfish.jersey.server.mvc.Template;

@Path("pidqueue")
public class PidQueueResource {

  @GET
  @Template(name = "/viewPidQueueDB.html.mustache")
  @RolesAllowed("admin")
  public ViewPidQueueDB viewPidQueueDB() {
    return new ViewPidQueueDB();
  }
}
