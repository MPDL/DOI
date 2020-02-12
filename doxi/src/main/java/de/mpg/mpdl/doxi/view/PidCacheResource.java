package de.mpg.mpdl.doxi.view;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.glassfish.jersey.server.mvc.Template;

@Path("pidcache")
public class PidCacheResource {

  @GET
  @Template(name = "/viewPidCacheDB.html.mustache")
  @RolesAllowed("admin")
  public ViewPidCacheDB viewPidCacheDB() {
    return new ViewPidCacheDB();
  }
}
