package de.mpg.mpdl.doxi.rest;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.glassfish.jersey.server.mvc.Template;

import de.mpg.mpdl.doxi.view.ViewPidCacheDB;

@Path("pidcache")
public class PidCacheResource {

  @GET
  @Template(name = "/viewPidCacheDB.html.mustache")
  @RolesAllowed("admin")
  public ViewPidCacheDB viewPidCacheDB() {
    return new ViewPidCacheDB();
  }
}
