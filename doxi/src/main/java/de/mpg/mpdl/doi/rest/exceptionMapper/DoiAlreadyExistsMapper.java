package de.mpg.mpdl.doi.rest.exceptionMapper;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import de.mpg.mpdl.doi.exception.DoiAlreadyExistsException;

@Provider
public class DoiAlreadyExistsMapper implements javax.ws.rs.ext.ExceptionMapper<DoiAlreadyExistsException>
{

	@Override
	public Response toResponse(DoiAlreadyExistsException exception) {
		System.out.println(exception.getMessage());
		return Response.status(Status.CONFLICT).type(MediaType.TEXT_PLAIN_TYPE).entity(exception.getMessage()).build();
	}
	
}