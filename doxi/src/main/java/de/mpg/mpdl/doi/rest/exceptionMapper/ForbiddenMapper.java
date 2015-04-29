package de.mpg.mpdl.doi.rest.exceptionMapper;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import de.mpg.mpdl.doi.exception.DoiAlreadyExistsException;

@Provider
public class ForbiddenMapper implements javax.ws.rs.ext.ExceptionMapper<ForbiddenException>
{

	@Override
	public Response toResponse(ForbiddenException exception) {
		return Response.status(Status.UNAUTHORIZED).entity(exception.getMessage()).header("WWW-Authenticate", "Basic realm=\"Please provide username and password\"").build();
	}
	
}