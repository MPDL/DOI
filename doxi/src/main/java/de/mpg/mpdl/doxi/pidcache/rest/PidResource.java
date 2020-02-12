package de.mpg.mpdl.doxi.pidcache.rest;

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
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.headers.*;
import io.swagger.v3.oas.annotations.info.Info;

@Path("/rest/v1")
@OpenAPIDefinition(
		info = @Info(
					title="PID API"
				),
		servers = @Server(
					url = "/doxi"
				)
)
//@Api(value = "PID API")
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
  @Operation(//
      summary = "Generates and register a Pid with known URL.",
      description = "ATTENTION: The Pid is saved only in the Pid Queue. If the Pid with URL already exists at GWDG it cannot be created!")
  @ApiResponses({//
      @ApiResponse(responseCode = "201", description = "PID created."),
      @ApiResponse(responseCode = "400", description = "PID not created.")})
  @POST
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @RolesAllowed(ROLE_USER)
  public Response create( //
      @Parameter(description = "the URL to which the PID should point", required = true) //
      @FormParam(URL) String url) throws DoxiException //
  {
    final String xml = pidService.create(URI.create(url));
    final Response response = Response.status(Status.CREATED).entity(xml).build();

    return response;
  }

  @Path(PATH_RETRIEVE)
  @Operation(summary = "Retrieves a Pid with known ID.")
  @ApiResponses({//
      @ApiResponse(responseCode = "200", description = "PID found."),
      @ApiResponse(responseCode = "400", description = "PID not found.")})
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes(MediaType.TEXT_PLAIN)
  @RolesAllowed(ROLE_USER)
  public Response retrieve( //
      @Parameter(description = "the ID which should be retrieved", required = true) //
      @QueryParam(ID) String id) //
      throws DoxiException {
    final String xml = pidService.retrieve(PidID.create(id));
    final Response response = Response.status(Status.OK).entity(xml).build();

    return response;
  }

  @Path(PATH_SEARCH)
  @Operation(summary = "Searches a Pid with known URL.")
  @ApiResponses({//
      @ApiResponse(responseCode = "200", description = "PID found."),
      @ApiResponse(responseCode = "400", description = "PID not found.")})
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes(MediaType.TEXT_PLAIN)
  @RolesAllowed(ROLE_USER)
  public Response search( //
      @Parameter(description = "the URL which should be searched", required = true) //
      @QueryParam(URL) String url) //
      throws DoxiException {
    final String s = pidService.search(URI.create(url));
    final Response response = Response.status(Status.OK).entity(s).build();

    return response;
  }

  @Path(PATH_UPDATE)
  @Operation(//
	  summary = "Updates an existing Pid.",
      description = "ATTENTION: The Pid is updated only in the Pid Queue. If the Pid does not exist at GWDG it cannot be updated!")
  @ApiResponses({//
      @ApiResponse(responseCode = "200", description = "PID updated."),
      @ApiResponse(responseCode = "400", description = "PID not updated.")})
  @PUT
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @RolesAllowed(ROLE_USER)
  public Response update( //
      @Parameter(description = "the ID of the existing Pid", required = true) //
      @FormParam(ID) String id, //
      @Parameter(description = "the URL to which the PID should point", required = true) //
      @FormParam(URL) String url) //
      throws DoxiException {
    final String xml = pidService.update(new Pid(PidID.create(id), URI.create(url)));
    final Response response = Response.status(Status.OK).entity(xml).build();

    return response;
  }

  @Path(PATH_CACHE_SIZE)
  @Operation(summary = "Returns the current size of the Pid Cache.")
  @ApiResponses({//
      @ApiResponse(responseCode = "200", description = "Size found."),
      @ApiResponse(responseCode = "400", description = "Size not found.")})
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
  @Operation(summary = "Returns the current size of the Pid Queue.")
  @ApiResponses({//
      @ApiResponse(responseCode = "200", description = "Size found."),
      @ApiResponse(responseCode = "400", description = "Size not found.")})
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
