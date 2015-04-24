package de.mpg.mpdl.doi.controller;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
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
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.ContextLoader;

import de.mpg.mpdl.doi.exception.DoiAlreadyExistsException;
import de.mpg.mpdl.doi.exception.DoiNotFoundException;
import de.mpg.mpdl.doi.exception.DoiRegisterException;
import de.mpg.mpdl.doi.exception.DoxiException;
import de.mpg.mpdl.doi.exception.MetadataInvalidException;
import de.mpg.mpdl.doi.exception.UrlInvalidException;
import de.mpg.mpdl.doi.model.DOI;
import de.mpg.mpdl.doi.util.PropertyReader;

@Singleton
public class DataciteAPIController implements DoiControllerInterface {

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
	public DOI getDOI(String doi) {
		DOI doiObject = new DOI();
		doiObject.setDoi(doi);
		Response doiResponse = dataciteTarget.path("doi").path(doi).request()
				.get();
		if (doiResponse.getStatus() == Response.Status.OK.getStatusCode()) {

		} else {

		}

		return doiObject;
	}

	/**
	 * Requesting Datacite service to List all DOIs already registered
	 * 
	 * @return response including list of all DOIs already registered
	 * @throws DoxiException 
	 */
	public List<DOI> getDOIList() throws DoxiException {
		List<DOI> doiList = new ArrayList<DOI>();
		Response response = dataciteTarget.path("doi").request().get();
		if (response.getStatus() == Response.Status.OK.getStatusCode()) {
			for (String listItem : response.readEntity(String.class)
					.split("\n")) {
				DOI doi = new DOI();
				doi.setDoi(listItem);
				doiList.add(doi);
			}
		} else {
			throw new DoxiException(response.getStatus(), response.getStatusInfo().toString());
		}
		return doiList;
	}

	public DOI createDOI(String url, String metadataXml) throws Exception {
		String doi = generateDoi();
		return createDOI(doi, url, metadataXml);
	}

	public DOI createDOI(String doi, String url, String metadataXml)
			throws DoxiException, DoiAlreadyExistsException,
			MetadataInvalidException, DoiRegisterException {

		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		
		logger.info("SecurityContext: " + SecurityContextHolder.getContext().getAuthentication().getName());
		
		Response getResp = dataciteTarget.path("doi").path(doi).request().get();
		if (getResp.getStatus() == Response.Status.OK.getStatusCode()
				|| getResp.getStatus() == Response.Status.NO_CONTENT
						.getStatusCode()) {
			logger.error("DOI " + doi + " already exists: "
					+ getResp.getStatusInfo() + getResp.getStatus() + " -- "
					+ getResp.readEntity(String.class));
			throw new DoiAlreadyExistsException(getResp.getStatus());
		} else if (getResp.getStatus() == Response.Status.NOT_FOUND
				.getStatusCode()) {

			// TODO URL check

			try {
				metadataXml = replaceDOIIdentifierInMetadataXml(metadataXml,
						doi);
				Response mdResp = createOrUpdateMetadata(metadataXml);
				if (mdResp.getStatus() == Response.Status.CREATED
						.getStatusCode()) {
					String respString = mdResp.readEntity(String.class);
					logger.info("Metadata uploaded successfully"
							+ mdResp.getStatusInfo() + mdResp.getStatus()
							+ " -- " + respString);
					DOI resultDoi = new DOI();
					resultDoi.setDoi(doi);
					resultDoi.setMetadata(respString);
					String entity = "doi=" + doi + "\nurl=" + url;
					Response doiResp = createOrUpdateUrl(entity);

					if (doiResp.getStatus() == Response.Status.CREATED
							.getStatusCode()) {
						logger.info("URL uploaded successfully "
								+ doiResp.getStatusInfo() + doiResp.getStatus()
								+ " -- " + doiResp.readEntity(String.class));
						resultDoi.setUrl(URI.create(url));
						return resultDoi;
					} else {
						logger.error("Problem with url upload "
								+ doiResp.getStatusInfo() + doiResp.getStatus()
								+ " -- " + doiResp.readEntity(String.class));
						throw new DoiRegisterException(doiResp.getStatus(),
								doiResp.readEntity(String.class));
					}

				} else {
					logger.error("Problem with metadata "
							+ mdResp.getStatusInfo() + mdResp.getStatus()
							+ " -- " + mdResp.readEntity(String.class));
					throw new MetadataInvalidException(metadataXml,
							mdResp.getStatus(), mdResp.readEntity(String.class));
				}
			} catch (Exception e) {
				logger.error("Problem replacing DOI in metadata", e);
				throw new DoxiException("Problem replacing DOI in metadata");
			}

		} else {
			logger.error("Problem with get DOI " + doi + " "
					+ getResp.getStatusInfo() + getResp.getStatus() + " -- "
					+ getResp.readEntity(String.class));
			throw new DoxiException(getResp.getStatus(),
					getResp.readEntity(String.class));
		}

	}

	@Override
	public DOI createDOIAutoGenerated(String url, String metadataXml)
			throws DoxiException, DoiNotFoundException,
			MetadataInvalidException, DoiRegisterException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DOI createDOIKnownSuffix(String suffix, String url,
			String metadataXml) throws DoxiException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void inactivateDOI(String doi) throws DoxiException {
		// TODO Auto-generated method stub
		logger.debug("");
		;

	}

	@Override
	public DOI updateDOI(String doi, String url, String metadataXml)
			throws DoxiException, DoiNotFoundException,
			MetadataInvalidException, DoiRegisterException {
		Response getResp = dataciteTarget.path("doi").path(doi).request().get();
		if (getResp.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
			logger.error("DOI " + doi + " is not existing: "
					+ getResp.getStatusInfo() + getResp.getStatus() + " -- "
					+ getResp.readEntity(String.class));
			throw new DoiNotFoundException(getResp.getStatus());
		} else if (getResp.getStatus() == Response.Status.OK.getStatusCode()
				|| getResp.getStatus() == Response.Status.NO_CONTENT
						.getStatusCode()) {
			try {
				metadataXml = replaceDOIIdentifierInMetadataXml(metadataXml,
						doi);

				Response mdResp = createOrUpdateMetadata(metadataXml);

				if (mdResp.getStatus() == Response.Status.CREATED
						.getStatusCode()) {
					logger.info("Metadata uploaded successfully"
							+ mdResp.getStatusInfo() + mdResp.getStatus()
							+ " -- " + mdResp.readEntity(String.class));
					DOI resultDoi = new DOI();
					resultDoi.setDoi(doi);
					resultDoi.setMetadata(mdResp.readEntity(String.class));
					String entity = "doi=" + doi + "\nurl=" + url;
					Response doiResp = createOrUpdateUrl(entity);

					if (doiResp.getStatus() == Response.Status.CREATED
							.getStatusCode()) {
						logger.info("URL uploaded successfully "
								+ doiResp.getStatusInfo() + doiResp.getStatus()
								+ " -- " + doiResp.readEntity(String.class));
						resultDoi.setUrl(URI.create(url));
						return resultDoi;
					} else {
						logger.error("Problem with url upload "
								+ doiResp.getStatusInfo() + doiResp.getStatus()
								+ " -- " + doiResp.readEntity(String.class));
						throw new DoiRegisterException(doiResp.getStatus(),
								doiResp.readEntity(String.class));
					}
				} else {
					logger.error("Problem with metadata "
							+ mdResp.getStatusInfo() + mdResp.getStatus()
							+ " -- " + mdResp.readEntity(String.class));
					throw new MetadataInvalidException(metadataXml,
							mdResp.getStatus(), mdResp.readEntity(String.class));
				}
			} catch (Exception e) {
				logger.error("Problem replacing DOI in metadata", e);
				throw new DoxiException("Problem replacing DOI in metadata");
			}
		} else {
			logger.error("Problem with get DOI " + doi + " "
					+ getResp.getStatusInfo() + getResp.getStatus() + " -- "
					+ getResp.readEntity(String.class));
			throw new DoxiException(getResp.getStatus(),
					getResp.readEntity(String.class));
		}

	}

	/*
	 * public Response updateUrl(String doi, String url) { String entity =
	 * "doi=" + doi + "\nurl=" + url; Response doiResp =
	 * dataciteTarget.path("doi")
	 * .request(MediaType.TEXT_PLAIN_TYPE).post(Entity.text(entity));
	 * 
	 * if (doiResp.getStatus() == Response.Status.CREATED.getStatusCode()) {
	 * logger.info("URL uploaded successfully " + doiResp.getStatusInfo() +
	 * doiResp.getStatus() + " -- " + doiResp.readEntity(String.class)); } else
	 * { logger.error("Problem with url upload " + doiResp.getStatusInfo() +
	 * doiResp.getStatus() + " -- " + doiResp.readEntity(String.class)); }
	 * return doiResp; }
	 */

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
			String doi) throws TransformerConfigurationException,
			TransformerException {
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
	private synchronized String generateDoi() {
		// Base36 encoding as Datacite DOI service is case insensitive
		String doiPrefix = Integer.toString(this.shortId, 36);
		this.shortId++;
		return doiPrefix;
	}

}
