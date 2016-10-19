package de.mpg.mpdl.doxi.rest.exceptionMapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import de.mpg.mpdl.doxi.exception.DoxiException;

@Provider
public class DoxiExceptionMapper implements ExceptionMapper<DoxiException> {
  @Override
  public Response toResponse(DoxiException exception) {
    return Response.status(Status.BAD_REQUEST).entity(exception.getMessage()).build();
  }
}
