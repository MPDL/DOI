package de.mpg.mpdl.doi.security;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.internal.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mpg.mpdl.doi.rest.JerseyApplicationConfig;

@PreMatching
@Provider
public class HttpBasicContainerRequestFilter implements ContainerRequestFilter {

	private static Logger logger = LoggerFactory.getLogger(HttpBasicContainerRequestFilter.class);
	
	@Override
	public void filter(ContainerRequestContext requestContext)
			throws IOException {
		// TODO Auto-generated method stub
		String auth = requestContext.getHeaderString("authorization");
		if (auth != null && auth.startsWith("Basic")) {
	        DoxiUser authenticatedUser;
			try {
				// Authorization: Basic base64credentials
				String base64Credentials = auth.substring("Basic".length()).trim();
				String credentials = Base64.decodeAsString(base64Credentials);
				// credentials = username:password
				final String[] values = credentials.split(":",2);
				//EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
				EntityManager em = JerseyApplicationConfig.emf.createEntityManager();
				
				
				TypedQuery<DoxiUser> qu = em.createQuery("select u from users u where u.username=?1 and u.password=?2", DoxiUser.class);
				qu.setParameter(1, values[0]);
				qu.setParameter(2, values[1]);
				authenticatedUser = qu.getSingleResult();
				em.close();
				requestContext.setSecurityContext(new Authorizer(authenticatedUser));
				return;
			} catch (Exception e) {
				logger.error("ERROR with Http basic authentication", e);
				//throw new ForbiddenException("Wrong credentials!");
			}
	        
	        
	        
		} 
			
		requestContext.setSecurityContext(new Authorizer(null));
		
	}
	
	

}
