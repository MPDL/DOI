package de.mpg.mpdl.doi.security;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@PreMatching
@Provider
public class HttpBasicContainerRequestFilter implements ContainerRequestFilter {

	Logger logger = LogManager.getLogger();
	
	@Override
	public void filter(ContainerRequestContext requestContext)
			throws IOException {
		// TODO Auto-generated method stub
		String auth = requestContext.getHeaderString("authorization");
		if(auth!=null)
		{
			
		}
		logger.info("RequestContext: " + requestContext);
	}
	
	

}
