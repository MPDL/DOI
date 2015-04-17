package de.mpg.mdpl.doi.rest.exceptionMapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import de.mpg.mdpl.doi.exception.DoiAlreadyExistsException;

@Provider
public class ExceptionMapper implements javax.ws.rs.ext.ExceptionMapper<Exception>
{

	@Override
	public Response toResponse(Exception exception) {
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(exception.getMessage()).build();
	}
	
}