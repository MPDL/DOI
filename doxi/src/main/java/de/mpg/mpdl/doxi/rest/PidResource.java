package de.mpg.mpdl.doxi.rest;

import java.net.URI;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import de.mpg.mpdl.doxi.exception.DoxiException;
import de.mpg.mpdl.doxi.pidcache.PidServiceInterface;
import de.mpg.mpdl.doxi.pidcache.model.Pid;
import de.mpg.mpdl.doxi.pidcache.model.PidID;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Path("pid")
@Api(value = "PID API")
public class PidResource {
  public static final String PATH_CACHE_SIZE = "cache/size";
  public static final String PATH_CREATE = "create";
  public static final String PATH_QUEUE_SIZE = "queue/size";
  public static final String PATH_RETRIEVE = "retrieve";
  public static final String PATH_SEARCH = "search";
  public static final String PATH_UPDATE = "update";

  private static final String ID = "id";
  private static final String URL = "url";

  private static final String ROLE_USER = "pid_user";

  @Inject
  private PidServiceInterface pidService;

  @Path(PATH_CREATE)
  @ApiOperation(//
      value = "Generates and register a Pid with known URL.",
      notes = "ATTENTION: The Pid is saved only in the Pid Queue. If the Pid with URL already exists at GWDG it cannot be created!")
  @ApiResponses({//
      @ApiResponse(code = 201, message = "PID created.", response = String.class),
      @ApiResponse(code = 400, message = "PID not created.")})
  @POST
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @RolesAllowed(ROLE_USER)
  public Response create( //
      @ApiParam(value = "the URL to which the PID should point", required = true) //
      @FormParam(URL) String url) throws DoxiException //
  {
    final String xml = pidService.create(URI.create(url));
    final Response response = Response.status(Status.CREATED).entity(xml).build();

    return response;
  }

  @Path(PATH_RETRIEVE)
  @ApiOperation(value = "Retrieves a Pid with known ID.")
  @ApiResponses({//
      @ApiResponse(code = 200, message = "PID found.", response = String.class),
      @ApiResponse(code = 400, message = "PID not found.")})
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes(MediaType.TEXT_PLAIN)
  @RolesAllowed(ROLE_USER)
  public Response retrieve( //
      @ApiParam(value = "the ID which should be retrieved", required = true) //
      @QueryParam(ID) String id) //
      throws DoxiException {
    final String xml = pidService.retrieve(PidID.create(id));
    final Response response = Response.status(Status.OK).entity(xml).build();

    return response;
  }

  @Path(PATH_SEARCH)
  @ApiOperation(value = "Searches a Pid with known URL.")
  @ApiResponses({//
      @ApiResponse(code = 200, message = "PID found.", response = String.class),
      @ApiResponse(code = 400, message = "PID not found.")})
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes(MediaType.TEXT_PLAIN)
  @RolesAllowed(ROLE_USER)
  public Response search( //
      @ApiParam(value = "the URL which should be searched", required = true) //
      @QueryParam(URL) String url) //
      throws DoxiException {
    final String s = pidService.search(URI.create(url));
    final Response response = Response.status(Status.OK).entity(s).build();

    return response;
  }

  @Path(PATH_UPDATE)
  @ApiOperation(//
      value = "Updates an existing Pid.",
      notes = "ATTENTION: The Pid is updated only in the Pid Queue. If the Pid does not exist at GWDG it cannot be updated!")
  @ApiResponses({//
      @ApiResponse(code = 200, message = "PID updated.", response = String.class),
      @ApiResponse(code = 400, message = "PID not updated.")})
  @PUT
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @RolesAllowed(ROLE_USER)
  public Response update( //
      @ApiParam(value = "the ID of the existing Pid", required = true) //
      @FormParam(ID) String id, //
      @ApiParam(value = "the URL to which the PID should point", required = true) //
      @FormParam(URL) String url) //
      throws DoxiException {
    final String xml = pidService.update(new Pid(PidID.create(id), URI.create(url)));
    final Response response = Response.status(Status.OK).entity(xml).build();

    return response;
  }

  @Path(PATH_CACHE_SIZE)
  @ApiOperation(value = "Returns the current size of the Pid Cache.")
  @ApiResponses({//
      @ApiResponse(code = 200, message = "Size found.", response = String.class),
      @ApiResponse(code = 400, message = "Size not found.")})
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes(MediaType.TEXT_PLAIN)
  @RolesAllowed(ROLE_USER)
  public Response cacheSize() //
      throws DoxiException {
    final long size = pidService.getCacheSize();
    final Response response = Response.status(Status.OK).entity("CacheSize: " + size).build();

    return response;
  }

  @Path(PATH_QUEUE_SIZE)
  @ApiOperation(value = "Returns the current size of the Pid Queue.")
  @ApiResponses({//
      @ApiResponse(code = 200, message = "Size found.", response = String.class),
      @ApiResponse(code = 400, message = "Size not found.")})
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes(MediaType.TEXT_PLAIN)
  @RolesAllowed(ROLE_USER)
  public Response queueSize() //
      throws DoxiException {
    final long size = pidService.getQueueSize();
    final Response response = Response.status(Status.OK).entity("QueueSize: " + size).build();

    return response;
  }
}
