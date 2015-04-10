package de.mpg.mdpl.doi.rest;

import javax.ws.rs.FormParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.mpg.mdpl.doi.controller.DataciteAPIController;

@Path("doi")
public class DOIResource {

	private static Logger logger = LogManager.getLogger();
	
	@Path("{doi:10\\..+/.+}")
	@PUT
	public void registerDOI(@PathParam("doi") String doi, @QueryParam("url") String url, String metadataXml)
	{
		try {
			new DataciteAPIController().createDOI(doi, url, metadataXml);
		} catch (Exception e) {
				logger.error("Error while registering DOI", e);
		}
		
	}
	
	
}
