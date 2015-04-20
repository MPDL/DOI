package de.mpg.mpdl.doi.rest.exceptionMapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.mpg.mpdl.doi.exception.DoxiException;

@Provider
public class DoxiExceptionMapper implements ExceptionMapper<DoxiException> {
	
	private static Logger logger = LogManager.getLogger();
	
	@Override
	public Response toResponse(DoxiException exception) {
		logger.error("Mapping", exception);
		return Response.status(Status.BAD_REQUEST).entity(exception.getMessage()).build();
	}


	

}
