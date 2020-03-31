package de.mpg.mpdl.doxi.doi.rest;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import de.mpg.mpdl.doxi.doi.controller.DoiControllerInterface;
import de.mpg.mpdl.doxi.doi.model.DOI;
import de.mpg.mpdl.doxi.exception.DoxiException;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.servers.Server;


@Path("/rest/v1")
@OpenAPIDefinition(
		info = @Info(
					title="DOI API",
					version = "1.1",
					contact = @Contact(
								name = "Max Planck Digital Library DOI Service",
								url = "https://doi.mpdl.mpg.de/"
							)
				),
		servers = @Server(
					url = "/doxi"
				),
		externalDocs = @ExternalDocumentation(
				url = "https://colab.mpdl.mpg.de/mediawiki/DOxI_Documentation",
				description = "DOI API Documentation"
				)
)
public class DOIResource {
	
	public static final String DATACITE_MD_V4_EXAMPLE = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
			"<resource xmlns=\"http://datacite.org/schema/kernel-4\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://datacite.org/schema/kernel-4 http://schema.datacite.org/meta/kernel-4/metadata.xsd\">\n" + 
			"	<identifier identifierType=\"DOI\"></identifier>\n" + 
			"	<titles>\n" + 
			"		<title>Sample Title</title>\n" + 
			"	</titles>\n" + 
			"	<creators>\n" + 
			"		<creator>\n" + 
			"			<creatorName>John Doe</creatorName>\n" + 
			"		</creator>\n" + 
			"	</creators>\n" + 
			"	<publisher>Sample Publisher</publisher>\n" + 
			"	<publicationYear>2020</publicationYear>\n" + 
			"	<resourceType resourceTypeGeneral=\"Other\"></resourceType>\n" +
			"</resource>";
	
	
	
	
	
	
	@Inject
	private DoiControllerInterface doiController;// = DataciteAPIController.getInstance();


	@Operation(summary = "Register a DOI with known value", 
			description = "Registers and mints a concrete DOI. The DOi in the given metadata XML is overwritten with the one provided in the path.",
			responses = {
					@ApiResponse(responseCode = "201", description = "DOI sucessfully created with metadata and URL. DOI is now findable."),
					@ApiResponse(responseCode = "202", description = "DOI metadata successfully registered. No URL was provided, DOI is still in draft state. Use the update method to register an URL and make the DOI findable."),
					@ApiResponse(responseCode = "409", description = "DOI already exists."),
					@ApiResponse(responseCode = "400", description = "DOI, URL or provided metadata have invalid format.") })
	@Path("{doi:10\\..+/.+}")
	@PUT
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes({ MediaType.TEXT_XML, MediaType.APPLICATION_XML })
	@RolesAllowed("user")
	public Response create(@Parameter(description = "the DOI to be registered", required = true) @PathParam("doi") String doi,
			@Parameter(description = "the URL to which this DOI should point. If no URL is given, a draft DOI is created (not findable). Use the update method to provide the URL later.", required = false) @QueryParam("url") String url,
			@RequestBody(description = "the metadata of this DOI in XML format. The identifier tag in the XML must either match the provided DOI or be empty (will be filled with provided DOI).", required = true,
				content = {
						@Content(mediaType = MediaType.TEXT_XML, examples = @ExampleObject(value = DOIResource.DATACITE_MD_V4_EXAMPLE)), 
						@Content(mediaType = MediaType.APPLICATION_XML, examples = @ExampleObject(value = DOIResource.DATACITE_MD_V4_EXAMPLE))}
			 ) String metadataXml)
			throws Exception {
		DOI resultDoi = doiController.createDOI(doi, url, metadataXml);
		
		// Return 201 if DOI was created and minted (metadata and URL were registered)
		// Return 202 if only metadata was registered (draft DOI)
		Status responseCode = resultDoi.getUrl() == null ? Status.ACCEPTED : Status.CREATED;
		
		Response r = Response.status(responseCode).entity(resultDoi.getDoi()).build();

		return r;
	}

	@Operation(summary = "Generate and register a DOI", description = "Generates, registers and mints (if URL is provided) a new DOI. If a certain suffix is required, it can be optionally provided. The DOi in the given metadata XML is overwritten with the generated one.")
	@ApiResponses({ @ApiResponse(responseCode = "201", description = "DOI sucessfully created with metadata and URL. DOI is now findable."),
			@ApiResponse(responseCode = "202", description = "DOI metadata successfully registered. No URL was provided, DOI is still in draft state. Use the update method to register an URL and make the DOI findable."),
			@ApiResponse(responseCode = "409", description = "DOI already exists."),
			@ApiResponse(responseCode = "400", description = "DOI, URL or provided metadata have invalid format.") })
	@PUT
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes({ MediaType.TEXT_XML, MediaType.APPLICATION_XML })
	@RolesAllowed("user")
	public Response createAutoOrSuffix(
			@Parameter(description = "the URL to which this DOI should point. If no URL is given, a draft DOI is created (not findable). Use the update method to provide the URL later.", required = false) @QueryParam("url") String url,
			@Parameter(description = "an optional suffix", required = false) @QueryParam("suffix") String suffix,
			@RequestBody(description = "the metadata of this DOI in XML format. The identifier tag in the XML must be empty, it will automatically be filled with the generated DOI.", required = true,
					content = {
							@Content(mediaType = MediaType.TEXT_XML, examples = @ExampleObject(value = DOIResource.DATACITE_MD_V4_EXAMPLE)), 
							@Content(mediaType = MediaType.APPLICATION_XML, examples = @ExampleObject(value = DOIResource.DATACITE_MD_V4_EXAMPLE))}
			) String metadataXml)
			throws DoxiException {
		DOI resultDoi = null;
		if (suffix == null) {
			resultDoi = doiController.createDOIAutoGenerated(url, metadataXml);
		} else {
			resultDoi = doiController.createDOIKnownSuffix(suffix, url, metadataXml);
		}

		// Return 201 if DOI was created and minted (metadata and URL were registered)
		// Return 202 if only metadata was registered (draft DOI)
		Status responseCode = resultDoi.getUrl() == null ? Status.ACCEPTED : Status.CREATED;

		Response r = Response.status(responseCode).entity(resultDoi.getDoi()).build();

		return r;
	}

	@Operation(summary = "Update an existing DOI", description = "Updates an existing DOI with a new URL and new metadata.")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "DOI sucessfully updated.", headers = {
					@Header(name = "Location", description = "the URL of this DOI") }),
			@ApiResponse(responseCode = "400", description = "DOI, URL or provided metadata have invalid format.") })
	@Path("{doi:10\\..+/.+}")
	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Consumes({ MediaType.TEXT_XML, MediaType.APPLICATION_XML })
	@RolesAllowed("user")
	public Response updateDOI(@Parameter(description = "the DOI to be updated", required = true) @PathParam("doi") String doi,
			@Parameter(description = "the new URL", required = true) @QueryParam("url") String url,
			@RequestBody(description = "the new metadata", required = true,
					content = {
							@Content(mediaType = MediaType.TEXT_XML, examples = @ExampleObject(value = DOIResource.DATACITE_MD_V4_EXAMPLE)), 
							@Content(mediaType = MediaType.APPLICATION_XML, examples = @ExampleObject(value = DOIResource.DATACITE_MD_V4_EXAMPLE))}
			) String metadataXml) throws DoxiException {

		DOI resultDoi = doiController.updateDOI(doi, url, metadataXml);

		return Response.status(Status.CREATED).entity(resultDoi.getMetadata())
				.header(HttpHeaders.LOCATION, resultDoi.getUrl().toString()).build();
	}

	
	@Operation(summary = "Get Metadata and URL of a DOI")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "DOI sucessfully retrieved and in state findable.",
					headers = {
					@Header(name = "Location", description = "the URL of this DOI") }),
			@ApiResponse(responseCode = "204", description = "DOI is in draft state, no URL available."),
			@ApiResponse(responseCode = "400", description = "DOI, URL or provided metadata have invalid format.") })
	@Path("{doi:10\\..+/.+}")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	@RolesAllowed("user")
	public Response getDOI(@Parameter(description = "the DOI", required = true, allowReserved = true) @PathParam("doi") String doi)
			throws DoxiException {
		DOI resultDoi = doiController.getDOI(doi);

		// Return 201 if DOI was created and minted (metadata and URL were registered)
		// Return 202 if only metadata was registered (draft DOI)
		Status responseCode = resultDoi.getUrl() == null ? Status.NO_CONTENT : Status.OK;
		
		return Response.status(responseCode).entity(resultDoi.getMetadata())
				.header(HttpHeaders.LOCATION, resultDoi.getUrl()!=null ? resultDoi.getUrl().toString() : "").build();
	}


	@Operation(summary = "Get a list of registered DOIs")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "DOIs sucessfully retrieved."),
			@ApiResponse(responseCode = "400", description = "DOI, URL or provided metadata have invalid format.") })
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@RolesAllowed("user")
	public Response getDOIList() throws DoxiException {
		// TODO DOI prefix depending on current user
		List<DOI> resultDoiList = doiController.getDOIList();
		StringBuffer sb = new StringBuffer();
		for (DOI doi : resultDoiList) {
			sb.append(doi.getDoi());
			sb.append("\n");
		}

		return Response.status(Status.OK).entity(sb.toString()).build();
	}


	@Operation(summary = "Deactivate a DOI")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "DOI sucessfully inactivated."),
			@ApiResponse(responseCode = "400", description = "Problem with inactivation.") })
	@Path("{doi:10\\..+/.+}")
	@DELETE
	@Produces(MediaType.APPLICATION_XML)
	@RolesAllowed("user")
	public Response inactivate(
			@Parameter(description = "the DOI to be inactivated", required = true) @PathParam("doi") String doi)
			throws DoxiException {
		DOI resultDoi = doiController.inactivateDOI(doi);

		return Response.status(Status.OK).entity(resultDoi.getMetadata()).build();
	}

}
