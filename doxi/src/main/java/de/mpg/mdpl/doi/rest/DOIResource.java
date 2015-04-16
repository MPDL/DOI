package de.mpg.mdpl.doi.rest;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.mpg.mdpl.doi.controller.DataciteAPIController;

@Path("doi")
public class DOIResource {

	private static Logger logger = LogManager.getLogger();

	@Path("{doi:10\\..+/.+}")
	@PUT
	@Produces("text/plain")
	public Response registerDOI(@PathParam("doi") String doi,
			@QueryParam("url") String url, String metadataXml) {
		Response r = Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				.build();
		try {
			logger.info("in PUT registerDOI(" + doi + ", " + url + ", "
					+ metadataXml);
			DataciteAPIController controller = DataciteAPIController
					.getInstance();
			logger.info("Controller: " + controller.toString());
			r = controller.createDOI(doi, url, metadataXml);
		} catch (Exception e) {
			logger.error("Error while registering DOI", e);
			return r;
		}
		return r;
	}

	@Path("{doi:10\\..+/.+}")
	@POST
	@Produces("text/plain")
	public Response updateDOI(@PathParam("doi") String doi, @QueryParam("url") String url,
			String metadataXml) {
		Response r = Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				.build();
		logger.info("in Post registerUnknownDOI(" + url + ", " + metadataXml);
		try {
			r = DataciteAPIController.getInstance().updateDOIMetadata(doi, url, metadataXml);
		} catch (Exception e) {
			logger.error("Error while registering DOI", e);
			return r;
		}
		return r;
	}

	@Path("{doi:10\\..+/.+}")
	@GET
	@Produces("text/plain")
	public Response getDOI(@PathParam("doi") String doi) {
		Response r = Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				.build();
		logger.info("in GET getDOI(" + doi + ")");
		try {
			r = DataciteAPIController.getInstance().getDOI(doi);
		} catch (Exception e) {
			logger.error("Error while registering DOI", e);
			return r;
		}
		return r;
	}

	@GET
	@Produces("text/plain")
	public Response getDOIList() {
		Response r = Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				.build();
		logger.info("in GET getDOIList()");
		try {
			r = DataciteAPIController.getInstance().getDOIList();
		} catch (Exception e) {
			logger.error("Error while registering DOI", e);
			return r;
		}
		return r;
	}

}
