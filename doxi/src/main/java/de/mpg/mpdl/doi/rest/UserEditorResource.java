package de.mpg.mpdl.doi.rest;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManager;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.glassfish.jersey.server.mvc.Template;

import com.google.common.eventbus.AllowConcurrentEvents;

import de.mpg.mpdl.doi.security.DoxiRole;
import de.mpg.mpdl.doi.security.DoxiUser;
import de.mpg.mpdl.doi.view.ViewUserDB;

@Path("useradmin")
public class UserEditorResource {

	
	@GET
	@Template(name="/viewUserDB.html.mustache")
	@RolesAllowed("admin")
	public ViewUserDB viewUserDB()
	{
		return new ViewUserDB();
	}
	
	
	@POST
	@Template(name="/viewUserDB.html.mustache")
	@RolesAllowed("admin")
	public ViewUserDB createUser(@FormParam("username") String username, @FormParam("email") String email, @FormParam("password") String password, @FormParam("prefix") String prefix, @FormParam("role") String role)
	{
		DoxiUser doxiUser = new DoxiUser();
		doxiUser.setUsername(username);
		doxiUser.setEmail(email);
		doxiUser.setPassword(password);
		doxiUser.setPrefix(prefix);
		List<DoxiRole> roles = new ArrayList<DoxiRole>();
		DoxiRole doxiRole = new DoxiRole();
		doxiRole.setRole(role);
		doxiRole.setUsername(username);
		roles.add(doxiRole);
		doxiUser.setRoles(roles);
		
		EntityManager em = JerseyApplicationConfig.emf.createEntityManager();
		em.getTransaction().begin();
		em.persist(doxiUser);
		em.getTransaction().commit();
		em.close();
		
		return new ViewUserDB();
		
	}
}
