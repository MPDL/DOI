package de.mpg.mpdl.doi.rest.exceptionMapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mpg.mpdl.doi.exception.DoxiException;

@Provider
public class DoxiExceptionMapper implements ExceptionMapper<DoxiException> {
	
	private static Logger logger = LoggerFactory.getLogger(DoxiExceptionMapper.class);
	
	@Override
	public Response toResponse(DoxiException exception) {
		return Response.status(Status.BAD_REQUEST).entity(exception.getMessage()).build();
	}


	

}
