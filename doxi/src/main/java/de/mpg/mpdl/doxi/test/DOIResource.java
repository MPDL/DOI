package de.mpg.mpdl.doxi.test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import de.mpg.mpdl.doxi.exception.DoxiException;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@Path("")
@OpenAPIDefinition(
		info = @Info(
					title="Test Rest API"
				),
		servers = @Server(
					url = "/doxi"
				)
)
public class DOIResource {
	
	@Operation(summary = "Test")
	@Path("")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response getTestResponse() throws DoxiException {
		
		return Response.status(Status.OK).entity("Hello World").build();
	}

	
}
