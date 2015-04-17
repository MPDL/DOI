package de.mpg.mdpl.doi.controller;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URLEncoder;

import javax.inject.Singleton;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.TransformerFactoryImpl;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.XQueryCompiler;
import net.sf.saxon.s9api.XdmNode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import de.mpg.mdpl.doi.util.PropertyReader;

@Singleton
public class DataciteAPIController {

	private final int RETRY_TIMEOUT = 1000; // Timeout until retrying request in
											// milliseconds

	private static DataciteAPIController instance = new DataciteAPIController();
	private int shortId;

	private static Logger logger = LogManager.getLogger();
	WebTarget dataciteTarget;

	private DataciteAPIController() {
		ClientConfig clientConfig = new ClientConfig();
		HttpAuthenticationFeature authFeature = HttpAuthenticationFeature
				.basic(PropertyReader.getProperty("datacite.api.login.user"),
						PropertyReader
								.getProperty("datacite.api.login.password"));
		clientConfig.register(authFeature);
		Client client = ClientBuilder.newClient(clientConfig);
		this.dataciteTarget = client.target(PropertyReader
				.getProperty("datacite.api.url"));
		// TODO change shortID to DB-auto-increment-key
		this.shortId = 0;
	}

	public static DataciteAPIController getInstance() {
		return instance;
	}

	/**
	 * @return xml
	 * @param doi
	 */
	public Response getDOI(String doi) {
		return dataciteTarget.path("doi").path(doi).request().get();
	}

	/**
	 * Requesting Datacite service to List all DOIs already registered
	 * 
	 * @return response including list of all DOIs already registered
	 */
	public Response getDOIList() {
		return dataciteTarget.path("doi").request().get();
	}

	public Response createDOI(String url, String metadataXml) throws Exception {
		String doi = generateDoi();
		return createDOI(doi, url, metadataXml);
	}

	public Response createDOI(String doi, String url, String metadataXml)
			throws Exception {

		Response getResp = dataciteTarget.path("doi").path(doi).request().get();
		if (getResp.getStatus() == Response.Status.OK.getStatusCode()
				|| getResp.getStatus() == Response.Status.NO_CONTENT
						.getStatusCode()) {
			logger.error("DOI " + doi + " already exists: "
					+ getResp.getStatusInfo() + getResp.getStatus() + " -- "
					+ getResp.readEntity(String.class));
			return Response.notModified("HANDLE_ALREADY_EXISTS").build();
		} else if (getResp.getStatus() == Response.Status.NOT_FOUND
				.getStatusCode()) {
			
			//TODO URL check
			
			metadataXml = replaceDOIIdentifierInMetadataXml(metadataXml, doi);

			Response mdResp = createOrUpdateMetadata(metadataXml);

			if (mdResp.getStatus() == Response.Status.CREATED.getStatusCode()) {
				logger.info("Metadata uploaded successfully"
						+ mdResp.getStatusInfo() + mdResp.getStatus() + " -- "
						+ mdResp.readEntity(String.class));

				String entity = "doi=" + doi + "\nurl=" + url;
				Response doiResp = createOrUpdateUrl(entity);

				if (doiResp.getStatus() == Response.Status.CREATED
						.getStatusCode()) {
					logger.info("URL uploaded successfully "
							+ doiResp.getStatusInfo() + doiResp.getStatus()
							+ " -- " + doiResp.readEntity(String.class));
					return Response.created(doiResp.getLocation()).build();
				} else {
					logger.error("Problem with url upload "
							+ doiResp.getStatusInfo() + doiResp.getStatus()
							+ " -- " + doiResp.readEntity(String.class));
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
				}

			} else {
				logger.error("Problem with metadata " + mdResp.getStatusInfo()
						+ mdResp.getStatus() + " -- "
						+ mdResp.readEntity(String.class));
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
			}

		} else {
			logger.error("Problem with get DOI " + doi + " "
					+ getResp.getStatusInfo() + getResp.getStatus() + " -- "
					+ getResp.readEntity(String.class));
			return Response.status(Response.Status.BAD_REQUEST).entity("Problem with get DOI").build();
		}

	}

	public Response updateDOIMetadata(String doi, String url, String metadataXml) throws Exception {
		Response getResp = dataciteTarget.path("doi").path(doi).request().get();
		if (getResp.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
			logger.error("DOI " + doi + " is not existing: "
					+ getResp.getStatusInfo() + getResp.getStatus() + " -- "
					+ getResp.readEntity(String.class));
			return getResp;
		} else if (getResp.getStatus() == Response.Status.OK.getStatusCode()
				|| getResp.getStatus() == Response.Status.NO_CONTENT
						.getStatusCode()){
			metadataXml = replaceDOIIdentifierInMetadataXml(metadataXml, doi);

			Response mdResp = createOrUpdateMetadata(metadataXml);

			if (mdResp.getStatus() == Response.Status.CREATED.getStatusCode()) {
				logger.info("Metadata uploaded successfully"
						+ mdResp.getStatusInfo() + mdResp.getStatus() + " -- "
						+ mdResp.readEntity(String.class));

				String entity = "doi=" + doi + "\nurl=" + url;
				Response doiResp = createOrUpdateUrl(entity);

				if (doiResp.getStatus() == Response.Status.CREATED
						.getStatusCode()) {
					logger.info("URL uploaded successfully "
							+ doiResp.getStatusInfo() + doiResp.getStatus()
							+ " -- " + doiResp.readEntity(String.class));
					return Response.created(doiResp.getLocation()).build();
				} else {
					logger.error("Problem with url upload "
							+ doiResp.getStatusInfo() + doiResp.getStatus()
							+ " -- " + doiResp.readEntity(String.class));
					return doiResp;
				}
			} else {
					logger.error("Problem with metadata " + mdResp.getStatusInfo()
							+ mdResp.getStatus() + " -- "
							+ mdResp.readEntity(String.class));
					return mdResp;
				}
		} else {
			logger.error("Problem with get DOI " + doi + " "
					+ getResp.getStatusInfo() + getResp.getStatus() + " -- "
					+ getResp.readEntity(String.class));
			return Response.status(Response.Status.BAD_REQUEST).entity("Problem with get DOI").build();
		}

	}

	public Response updateUrl(String doi, String url) {
		String entity = "doi=" + doi + "\nurl=" + url;
		Response doiResp = dataciteTarget.path("doi")
				.request(MediaType.TEXT_PLAIN_TYPE).post(Entity.text(entity));

		if (doiResp.getStatus() == Response.Status.CREATED.getStatusCode()) {
			logger.info("URL uploaded successfully " + doiResp.getStatusInfo()
					+ doiResp.getStatus() + " -- "
					+ doiResp.readEntity(String.class));
		} else {
			logger.error("Problem with url upload " + doiResp.getStatusInfo()
					+ doiResp.getStatus() + " -- "
					+ doiResp.readEntity(String.class));
		}
		return doiResp;
	}

	/**
	 * 
	 * @param metadataXml
	 * @return Http Response of the Datacite service
	 */
	public Response createOrUpdateMetadata(String metadataXml) {
		return dataciteTarget.path("metadata")
				.request(MediaType.TEXT_PLAIN_TYPE)
				.post(Entity.xml(metadataXml));
	}

	private Response createOrUpdateUrl(String doiAndUrl) {
		return dataciteTarget.path("doi").request(MediaType.TEXT_PLAIN_TYPE)
				.post(Entity.text(doiAndUrl));
	}

	private String replaceDOIIdentifierInMetadataXml(String metadataXml,
			String doi) throws Exception {
		TransformerFactory transFact = new TransformerFactoryImpl();
		InputStream stylesheet = DataciteAPIController.class
				.getResourceAsStream("/replace-doi.xsl");
		Transformer trans = transFact.newTransformer(new StreamSource(
				stylesheet));
		trans.setParameter("doi", doi);

		StringWriter writer = new StringWriter();
		trans.transform(new StreamSource(new StringReader(metadataXml)),
				new StreamResult(writer));
		return writer.toString();

		/*
		 * Processor proc = new Processor(false); XdmNode node =
		 * proc.newDocumentBuilder().build(new StreamSource(new
		 * StringReader(metadataXml)));
		 * 
		 * XQueryCompiler compiler = proc.newXQueryCompiler();
		 * compiler.setEncoding("UTF-8"); String query = ""
		 */

	}

	/**
	 * 
	 * @return a not yet registered DOI
	 * @throws Exception
	 */
	// TODO maybe add parameter service ID?
	private synchronized String generateDoi() throws Exception {
		// Base36 encoding as Datacite DOI service is case insensitive
		String doiPrefix = Integer.toString(this.shortId, 36);
		this.shortId++;
		return doiPrefix;
	}

}
