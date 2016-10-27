package de.mpg.mpdl.doxi.rest;

import java.net.URI;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import de.mpg.mpdl.doxi.pidcache.Pid;
import de.mpg.mpdl.doxi.pidcache.PidID;
import de.mpg.mpdl.doxi.pidcache.PidServiceInterface;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Path("pid")
@Api(value = "MPDL DOXI PID REST API")
public class PidResource {
  private static final String PATH_CACHE_SIZE = "cache/size";
  private static final String PATH_CREATE = "create";
  private static final String PATH_QUEUE_SIZE = "queue/size";
  private static final String PATH_RETRIEVE = "retrieve";
  private static final String PATH_SEARCH = "search";
  private static final String PATH_UPDATE = "update";
  
  private static final String PID = "pid";
  private static final String URL = "url";
  
  private static final String ROLE_USER = "user";
  
  @Inject
  private PidServiceInterface pidService;

  @Path(PATH_CREATE)
  @ApiOperation(//
      value = "Generates and register a Pid with known URL.",
      notes = "If Pid with URL already exists it cannot be created.")
  @ApiResponses({
      @ApiResponse(code = 201, message = "PID created.", response = String.class),
      @ApiResponse(code = 400, message = "PID not created.")})
  @POST
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes({MediaType.TEXT_XML, MediaType.APPLICATION_XML})
  @RolesAllowed(ROLE_USER)
  public Response create( //
      @ApiParam(value = "the URL to which the PID should point", required = true) //
      @QueryParam(URL) String url) //
      throws Exception {
    final String resultPid = pidService.create(URI.create(url));
    final Response response = Response.status(Status.CREATED).entity(resultPid).build();

    return response;
  }

  @Path(PATH_RETRIEVE)
  @ApiOperation(value = "Retrieves a Pid with known ID.")
  @ApiResponses({
      @ApiResponse(code = 200, message = "PID found.", response = String.class),
      @ApiResponse(code = 400, message = "PID not found.")})
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes({MediaType.TEXT_XML, MediaType.APPLICATION_XML})
  @RolesAllowed(ROLE_USER)
  public Response retrieve( //
      @ApiParam(value = "the ID which should be retrieved", required = true) //
      @QueryParam(PID) String id) //
      throws Exception {
    final String resultPid = pidService.retrieve(PidID.create(id));
    final Response response = Response.status(Status.OK).entity(resultPid).build();

    return response;
  }
  
  @Path(PATH_SEARCH)
  @ApiOperation(value = "Searches a Pid with known URL.")
  @ApiResponses({
      @ApiResponse(code = 200, message = "PID found.", response = String.class),
      @ApiResponse(code = 400, message = "PID not found.")})
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes({MediaType.TEXT_XML, MediaType.APPLICATION_XML})
  @RolesAllowed(ROLE_USER)
  public Response search( //
      @ApiParam(value = "the URL which should be searched", required = true) //
      @QueryParam(URL) String url) //
      throws Exception {
    final String resultPid = pidService.search(URI.create(url));
    final Response response = Response.status(Status.OK).entity(resultPid).build();

    return response;
  }

  @Path(PATH_UPDATE)
  @ApiOperation(value = "Updates an existing Pid.")
  @ApiResponses({
      @ApiResponse(code = 200, message = "PID updated.", response = String.class),
      @ApiResponse(code = 400, message = "PID not updated.")})
  @PUT
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes({MediaType.TEXT_XML, MediaType.TEXT_XML})
  @RolesAllowed(ROLE_USER)
  public Response update( //
      @ApiParam(value = "the ID of the existing Pid", required = true) //
      @QueryParam("id") String id, //
      @ApiParam(value = "the URL to which the PID should point", required = true) //
      @QueryParam(URL) String url) //
      throws Exception {
    final String resultPid = pidService.update(new Pid(PidID.create(id), URI.create(url)));
    final Response response = Response.status(Status.OK).entity(resultPid).build();

    return response;
  }
  
  @Path(PATH_CACHE_SIZE)
  @ApiOperation(value = "Returns the current size of the Pid Cache.")
  @ApiResponses({
      @ApiResponse(code = 200, message = "Size found.", response = String.class),
      @ApiResponse(code = 400, message = "Size not found.")})
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes({MediaType.TEXT_XML, MediaType.APPLICATION_XML})
  @RolesAllowed(ROLE_USER)
  public Response cacheSize() //
      throws Exception {
    final long size = pidService.getCacheSize();
    final Response response = Response.status(Status.OK).entity("CacheSize: " + size).build();

    return response;
  }

  @Path(PATH_QUEUE_SIZE)
  @ApiOperation(value = "Returns the current size of the Pid Queue.")
  @ApiResponses({
      @ApiResponse(code = 200, message = "Size found.", response = String.class),
      @ApiResponse(code = 400, message = "Size not found.")})
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes({MediaType.TEXT_XML, MediaType.APPLICATION_XML})
  @RolesAllowed(ROLE_USER)
  public Response queueSize() //
      throws Exception {
    final long size = pidService.getQueueSize();
    final Response response = Response.status(Status.OK).entity("QueueSize: " + size).build();

    return response;
  }
}