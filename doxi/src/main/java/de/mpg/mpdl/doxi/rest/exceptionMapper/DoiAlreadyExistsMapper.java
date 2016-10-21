package de.mpg.mpdl.doxi.rest.exceptionMapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import de.mpg.mpdl.doxi.exception.DoiAlreadyExistsException;

@Provider
public class DoiAlreadyExistsMapper implements ExceptionMapper<DoiAlreadyExistsException> {
  @Override
  public Response toResponse(DoiAlreadyExistsException exception) {
    return Response.status(Status.CONFLICT).type(MediaType.TEXT_PLAIN_TYPE)
        .entity(exception.getMessage()).build();
  }
}
