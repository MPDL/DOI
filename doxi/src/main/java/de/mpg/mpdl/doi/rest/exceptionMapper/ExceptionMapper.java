package de.mpg.mpdl.doi.rest.exceptionMapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.mpg.mpdl.doi.exception.DoiAlreadyExistsException;


public class ExceptionMapper implements javax.ws.rs.ext.ExceptionMapper<Exception>
{
	private static Logger logger = LogManager.getLogger();

	@Override
	public Response toResponse(Exception exception) {
		logger.error("Mapping", exception);
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(exception.getMessage()).build();
	}
	
}