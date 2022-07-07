package de.mpg.mpdl.doxi.security;

import java.io.IOException;
import java.util.Base64;

import jakarta.persistence.EntityManager;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;


import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mpg.mpdl.doxi.util.EMF;

@PreMatching
@Provider
public class HttpBasicContainerRequestFilter implements ContainerRequestFilter {

  private static final Logger LOG = LoggerFactory.getLogger(HttpBasicContainerRequestFilter.class);

  @Override
  public void filter(ContainerRequestContext requestContext) throws IOException {
    String auth = requestContext.getHeaderString("authorization");
    
    if (auth != null && auth.startsWith("Basic")) {
      try {
        String base64Credentials = auth.substring("Basic".length()).trim();
        String credentials = new String(Base64.getDecoder().decode(base64Credentials));
        final String[] values = credentials.split(":", 2);
        
        EntityManager em = EMF.emf.createEntityManager();
        final DoxiUser authenticatedUser = em.find(DoxiUser.class, values[0]);
        em.close();

        boolean authenticated = authenticatedUser!= null && BCrypt.checkpw(values[1], authenticatedUser.getPassword());
        
        if (authenticated) {
          requestContext.setSecurityContext(new Authorizer(authenticatedUser));
          return;
        } else {
          LOG.warn("User " + values[0] + " unknown or user provided a wrong password, proceeding with anonymous");
        }
      } catch (Exception e) {
        LOG.warn("ERROR with Http basic authentication, proceeding with anonymous", e);
        // throw new ForbiddenException("Wrong credentials!");
      }
    }
    
    requestContext.setSecurityContext(new Authorizer(null));
  }
}