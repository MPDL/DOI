package de.mpg.mpdl.doxi.rest;

import java.net.URI;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import de.mpg.mpdl.doxi.pidcache.PidCacheServiceInterface;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Path("pid")
@Api(value = "MPDL DOXI PID REST API")
public class PidResource {
  @Inject
  private PidCacheServiceInterface pidCacheService;

  @ApiOperation(//
      value = "Register a Pid with known URL.",
      notes = "Register a Pid with known URL.")
  @ApiResponses({
      @ApiResponse(code = 201, message = "PID sucessfully created.", response = String.class),
      @ApiResponse(code = 400, message = "PID not created.")})
  @PUT
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes({MediaType.TEXT_XML, MediaType.APPLICATION_XML})
  @RolesAllowed("user")
  public Response create( //
      @ApiParam(value = "the URL to which the PID should point", required = true) //
      @QueryParam("url") String url) //
      throws Exception {
    final String resultPid = pidCacheService.create(URI.create(url));
    final Response r = Response.status(Status.CREATED).entity(resultPid).build();

    return r;
  }
}