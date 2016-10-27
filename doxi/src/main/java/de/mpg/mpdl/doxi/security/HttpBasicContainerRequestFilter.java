package de.mpg.mpdl.doxi.security;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.internal.util.Base64;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mpg.mpdl.doxi.rest.JerseyApplicationConfig;

@PreMatching
@Provider
public class HttpBasicContainerRequestFilter implements ContainerRequestFilter {

  private static final Logger LOG = LoggerFactory.getLogger(HttpBasicContainerRequestFilter.class);

  @Override
  public void filter(ContainerRequestContext requestContext) throws IOException {
    // TODO Auto-generated method stub
    String auth = requestContext.getHeaderString("authorization");
    if (auth != null && auth.startsWith("Basic")) {
      DoxiUser authenticatedUser;
      try {
        // Authorization: Basic base64credentials
        String base64Credentials = auth.substring("Basic".length()).trim();
        String credentials = Base64.decodeAsString(base64Credentials);
        // credentials = username:password
        final String[] values = credentials.split(":", 2);
        // EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
        EntityManager em = JerseyApplicationConfig.emf.createEntityManager();
        authenticatedUser = em.find(DoxiUser.class, values[0]);
        em.close();

        boolean authenticated = BCrypt.checkpw(values[1], authenticatedUser.getPassword());
        if (authenticated) {
          requestContext.setSecurityContext(new Authorizer(authenticatedUser));
          return;
        } else {
          LOG.warn("User " + values[0] + " provided a wrong password, proceeding with anonymous");
        }
      } catch (Exception e) {
        LOG.warn("ERROR with Http basic authentication, proceeding with anonymous", e);
        // throw new ForbiddenException("Wrong credentials!");
      }
    }
    requestContext.setSecurityContext(new Authorizer(null));
  }
}