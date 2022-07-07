package de.mpg.mpdl.doxi.view;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import jakarta.persistence.EntityManager;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.glassfish.jersey.server.mvc.Template;

import de.mpg.mpdl.doxi.security.DoxiRole;
import de.mpg.mpdl.doxi.security.DoxiUser;
import de.mpg.mpdl.doxi.util.EMF;

@Path("useradmin")
public class UserEditorResource {

  @GET
  @Template(name = "/viewUserDB.html.mustache")
  @RolesAllowed("admin")
  public ViewUserDB viewUserDB() {
    return new ViewUserDB();
  }

  @POST
  @Template(name = "/viewUserDB.html.mustache")
  @RolesAllowed("admin")
  public ViewUserDB createUser(@FormParam("username") String username,
      @FormParam("email") String email, @FormParam("password") String password,
      @FormParam("prefix") String prefix, @FormParam("role") String sRoles) {

    DoxiUser doxiUser = new DoxiUser();
    doxiUser.setUsername(username);
    doxiUser.setEmail(email);
    doxiUser.setPassword(password);
    doxiUser.setPrefix(prefix);
    List<DoxiRole> roles = new ArrayList<DoxiRole>();
    for (String role : sRoles.split(";")) {
      DoxiRole doxiRole = new DoxiRole();
      doxiRole.setRole(role);
      doxiRole.setUsername(username);
      roles.add(doxiRole);
    }
    doxiUser.setRoles(roles);

    EntityManager em = EMF.emf.createEntityManager();
    em.getTransaction().begin();
    em.persist(doxiUser);
    em.getTransaction().commit();
    em.close();

    return new ViewUserDB();
  }
}
