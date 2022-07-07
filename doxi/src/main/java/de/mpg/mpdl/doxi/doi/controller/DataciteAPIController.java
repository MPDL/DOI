package de.mpg.mpdl.doxi.doi.controller;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mpg.mpdl.doxi.doi.model.DOI;
import de.mpg.mpdl.doxi.exception.DoiAlreadyExistsException;
import de.mpg.mpdl.doxi.exception.DoiInvalidException;
import de.mpg.mpdl.doxi.exception.DoiNotFoundException;
import de.mpg.mpdl.doxi.exception.DoiRegisterException;
import de.mpg.mpdl.doxi.exception.DoxiException;
import de.mpg.mpdl.doxi.exception.MetadataInvalidException;
import de.mpg.mpdl.doxi.security.DoxiRole;
import de.mpg.mpdl.doxi.security.DoxiUser;
import de.mpg.mpdl.doxi.util.EMF;
import de.mpg.mpdl.doxi.util.PropertyReader;
import net.sf.saxon.TransformerFactoryImpl;
import net.sf.saxon.expr.instruct.TerminationException;

/**
 * Implementation of the DoiControllerInterface
 * 
 * @author walter
 * 
 */
public class DataciteAPIController implements DoiControllerInterface {
	private static final Logger LOG = LoggerFactory.getLogger(DataciteAPIController.class);

	private WebTarget dataciteTarget;

	@Context
	private SecurityContext secContext;

	public DataciteAPIController() {
		ClientConfig clientConfig = new ClientConfig();

		HttpAuthenticationFeature authFeature = HttpAuthenticationFeature.basic( //
				PropertyReader.getProperty(PropertyReader.DOXI_DOI_DATACITE_API_LOGIN_USER),
				PropertyReader.getProperty(PropertyReader.DOXI_DOI_DATACITE_API_LOGIN_PASSWORD));
		clientConfig.register(authFeature);

		Client client = ClientBuilder.newClient(clientConfig);
		client.register(new LoggingFeature(
				java.util.logging.Logger.getLogger(this.getClass().getCanonicalName())));

		this.dataciteTarget = client.target(PropertyReader.getProperty(PropertyReader.DOXI_DOI_DATACITE_API_URL));

		String testMode = PropertyReader.getProperty(PropertyReader.DOXI_DOI_DATACITE_API_TESTMODE);
		if (testMode != null && testMode.equals("true")) {
			this.dataciteTarget = dataciteTarget.queryParam("testMode", "true");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.mpg.mpdl.doi.controller.DoiControllerInterface#getDOI(java.lang.String )
	 */
	public DOI getDOI(String doi) throws DoxiException, DoiNotFoundException {
		LOG.info("User " + secContext.getUserPrincipal() + " requests getDoi() with doi " + doi);

		DOI doiObject = new DOI();
		doiObject.setDoi(doi);
		Response doiResponse = dataciteTarget.path("doi").path(doi).request().get();
		//OK 200 = DOI findable,
		//NO CONTENT 204 = DOI in draft
		if (doiResponse.getStatus() == Response.Status.OK.getStatusCode() || doiResponse.getStatus() == Response.Status.NO_CONTENT.getStatusCode()) {
			try {
				doiObject.setDoi(doi);
				//Only set url if DOI is in findable state
				if(doiResponse.getStatus() == Response.Status.OK.getStatusCode())
				{
					doiObject.setUrl(new URI(doiResponse.readEntity(String.class)));
				}
				Response doiMetaDataResponse = dataciteTarget.path("metadata").path(doi).request().get();
				if (doiMetaDataResponse.getStatus() == Response.Status.OK.getStatusCode()) {
					doiObject.setMetadata(doiMetaDataResponse.readEntity(String.class));
				} else {
					LOG.error("Error getting DOI metadata");
					// TODO DOI maybe another exception type?
					throw new DoxiException(doiMetaDataResponse.getStatus(), doiMetaDataResponse.readEntity(String.class));
				}
			} catch (URISyntaxException e) {
				LOG.error("Error setting URL", e);
				throw new DoxiException(e);
			}
		} else {
			LOG.error("Error getting DOI");
			throw new DoiNotFoundException(doiResponse.getStatus(), doiResponse.readEntity(String.class));
		}
		LOG.info("getDoi() successfully returned doi " + doiObject.getDoi());
		return doiObject;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mpg.mpdl.doi.controller.DoiControllerInterface#getDOIList()
	 */
	public List<DOI> getDOIList() throws DoxiException {
		LOG.info("User " + secContext.getUserPrincipal() + " requests getDoiList()");

		List<DOI> doiList = new ArrayList<DOI>();
		Response response = dataciteTarget.path("doi").request().get();

		if (response.getStatus() == Response.Status.OK.getStatusCode()) {
			for (String listItem : response.readEntity(String.class).split("\n")) {
				if (listItem != null
						&& prefixAllowed(listItem)) {
					DOI doi = new DOI();
					doi.setDoi(listItem);
					doiList.add(doi);
				}
			}
		} else {
			throw new DoxiException(response.getStatus(), response.getStatusInfo().toString());
		}
		LOG.info("getDoiList() successfully returned dois ");
		return doiList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mpg.mpdl.doi.controller.DoiControllerInterface#createDOI(java.lang
	 * .String, java.lang.String, java.lang.String)
	 */
	public DOI createDOI(String doi, String url, String metadataXml)
			throws DoxiException, DoiAlreadyExistsException, DoiRegisterException {

		LOG.info("User " + secContext.getUserPrincipal() + " requests createDoi() with doi " + doi + " and url " + url);

		LOG.debug("Metadata: " + metadataXml);
		if (doi == null || !prefixAllowed(doi)) {
			DoxiUser currentUser = (DoxiUser) secContext.getUserPrincipal();
			throw new DoiInvalidException("Prefix not allowed for this user, only the following is valid: " + currentUser.getPrefix() );
		}

		// Check if DOI already exists
		Response getResp = dataciteTarget.path("doi").path(doi).request().get();

		if (getResp.getStatus() == Response.Status.OK.getStatusCode()
				|| getResp.getStatus() == Response.Status.NO_CONTENT.getStatusCode()) {
			// DOI already exists, throw error
			String resp = getResp.readEntity(String.class);
			String message = "DOI " + doi + " already exists.";
			if (getResp.getStatus() == Response.Status.OK.getStatusCode()) {
				message = message + " It points to: " + resp;
			} else {
				message = message + " It is not minted yet, means it has no URL attached.";
			}

			LOG.error(message + getResp.getStatusInfo() + getResp.getStatus() + " -- " + resp);
			throw new DoiAlreadyExistsException(getResp.getStatus(), message);

		} else if (getResp.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
			// DOI does not exist yet
			metadataXml = replaceDOIIdentifierInMetadataXml(metadataXml, doi);

			Response mdResp;
			try {
				mdResp = createOrUpdateMetadata(metadataXml);
			} catch (Exception e) {
				LOG.error("Problem while uploading metadata", e);
				throw new DoxiException("Problem while uploading metadata", e);
			}

			if (mdResp.getStatus() == Response.Status.CREATED.getStatusCode()) {

				// Metadata registered successfully, now register url (only if given)
				String metaDataResponseEntity = mdResp.readEntity(String.class);
				LOG.info("Metadata uploaded successfully" + mdResp.getStatusInfo() + mdResp.getStatus() + " -- "
						+ metaDataResponseEntity);
				DOI resultDoi = new DOI();
				resultDoi.setDoi(doi);
				resultDoi.setMetadata(metaDataResponseEntity);

				if (url == null) {
					// No url given, DOI is in draft state

					LOG.info("As no URL was given the DOI is in draft state only!");
					LOG.info("createDoi() successfully returned with doi (in draft state!!) " + resultDoi.getDoi());
					return resultDoi;
				} else {
					// url was given, trying to mint
					Response doiResp;
					try {
						String entity = "doi=" + doi + "\nurl=" + url;
						doiResp = createOrUpdateUrl(entity);
					} catch (Exception e) {
						LOG.error("Problem while minting DOI", e);
						throw new DoxiException("Problem while minting DOI", e);
					}

					if (doiResp.getStatus() == Response.Status.CREATED.getStatusCode()) {
						LOG.info("URL uploaded successfully " + doiResp.getStatusInfo() + doiResp.getStatus() + " -- "
								+ doiResp.readEntity(String.class));
						resultDoi.setUrl(URI.create(url));
						LOG.info("createDoi() successfully returned with doi " + resultDoi.getDoi());
						return resultDoi;
					} else {
						String doiResponseEntity = doiResp.readEntity(String.class);
						LOG.error("Problem with url upload " + doiResp.getStatusInfo() + doiResp.getStatus() + " -- "
								+ doiResponseEntity);
						throw new DoiRegisterException(doiResp.getStatus(), doiResponseEntity);
					}
				}

			} else {
				// Metadata registration failed
				String metaDataResponseEntity = mdResp.readEntity(String.class);
				LOG.error("Problem with metadata " + mdResp.getStatusInfo() + mdResp.getStatus() + " -- "
						+ metaDataResponseEntity);
				throw new MetadataInvalidException(metadataXml, mdResp.getStatus(), metaDataResponseEntity);
			}

		} else {
			// Unexpected return code of get DOI
			String getDoiResponseEntity = getResp.readEntity(String.class);
			LOG.error("Problem with get DOI " + doi + " " + getResp.getStatusInfo() + getResp.getStatus() + " -- "
					+ getDoiResponseEntity);
			throw new DoxiException(getResp.getStatus(), getDoiResponseEntity);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mpg.mpdl.doi.controller.DoiControllerInterface#createDOIAutoGenerated
	 * (java.lang.String, java.lang.String)
	 */
	@Override
	public DOI createDOIAutoGenerated(String url, String metadataXml)
			throws DoxiException, DoiNotFoundException, MetadataInvalidException, DoiRegisterException {
		return createDOI(generateDoi(), url, metadataXml);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mpg.mpdl.doi.controller.DoiControllerInterface#createDOIKnownSuffix
	 * (java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public DOI createDOIKnownSuffix(String suffix, String url, String metadataXml)
			throws DoxiException, DoiAlreadyExistsException, MetadataInvalidException, DoiRegisterException {
		return createDOI(getDoiPrefix() + suffix, url, metadataXml);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mpg.mpdl.doi.controller.DoiControllerInterface#inactivateDOI(java.
	 * lang.String)
	 */
	@Override
	public DOI inactivateDOI(String doi) throws DoxiException {
		LOG.info("User " + secContext.getUserPrincipal() + " requests inactivateDoi() with doi " + doi);
		Response resp = dataciteTarget.path("metadata").path(doi).request().delete();
		String respMessage = resp.readEntity(String.class);
		if (resp.getStatus() == Response.Status.OK.getStatusCode()) {
			LOG.info("inactivateDOI() successfully inactivated doi " + doi);
			DOI retDoi = new DOI();
			retDoi.setMetadata(respMessage);
			retDoi.setDoi(doi);
			return retDoi;
		} else {
			throw new DoxiException(resp.getStatus(), respMessage);
		}
	}

	@Override
	public DOI updateDOI(String doi, String url, String metadataXml)
			throws DoxiException, DoiNotFoundException, MetadataInvalidException, DoiRegisterException {
		LOG.info("User " + secContext.getUserPrincipal() + " requests updateDoi() with doi " + doi + " and url " + url);
		LOG.info("Metadata: " + metadataXml);
		Response getResp = dataciteTarget.path("doi").path(doi).request().get();
		if (getResp.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
			LOG.error("DOI " + doi + " does not exist: " + getResp.getStatusInfo() + getResp.getStatus() + " -- "
					+ getResp.readEntity(String.class));
			throw new DoiNotFoundException(getResp.getStatus());
		} else if (getResp.getStatus() == Response.Status.OK.getStatusCode()
				|| getResp.getStatus() == Response.Status.NO_CONTENT.getStatusCode()) {

			metadataXml = replaceDOIIdentifierInMetadataXml(metadataXml, doi);

			Response mdResp;
			try {
				mdResp = createOrUpdateMetadata(metadataXml);
			} catch (Exception e) {
				LOG.error("Problem while uploading metadata", e);
				throw new DoxiException("Problem while uploading metadata", e);
			}

			if (mdResp.getStatus() == Response.Status.CREATED.getStatusCode()) {
				String metadataResponseEntity = mdResp.readEntity(String.class);
				LOG.info("Metadata uploaded successfully" + mdResp.getStatusInfo() + mdResp.getStatus() + " -- "
						+ metadataResponseEntity);
				DOI resultDoi = new DOI();
				resultDoi.setDoi(doi);
				resultDoi.setMetadata(metadataResponseEntity);
				String entity = "doi=" + doi + "\nurl=" + url;

				Response doiResp;
				try {
					doiResp = createOrUpdateUrl(entity);
				} catch (Exception e) {
					LOG.error("Problem while minting DOI/applying URL", e);
					throw new DoxiException("Problem while minting DOI/applying URL", e);
				}

				if (doiResp.getStatus() == Response.Status.CREATED.getStatusCode()) {
					LOG.info("URL uploaded successfully " + doiResp.getStatusInfo() + doiResp.getStatus() + " -- "
							+ doiResp.readEntity(String.class));
					resultDoi.setUrl(URI.create(url));
					LOG.info("updateDoi() successfully returned with doi " + resultDoi.getDoi());
					return resultDoi;
				} else {
					String registerDoiResponseEntity = doiResp.readEntity(String.class);
					LOG.error("Problem with url upload " + doiResp.getStatusInfo() + doiResp.getStatus() + " -- "
							+ registerDoiResponseEntity);
					throw new DoiRegisterException(doiResp.getStatus(), registerDoiResponseEntity);
				}
			} else {
				String metadataResponseEntity = mdResp.readEntity(String.class);
				LOG.error("Problem with metadata " + mdResp.getStatusInfo() + mdResp.getStatus() + " -- "
						+ metadataResponseEntity);
				throw new MetadataInvalidException(metadataXml, mdResp.getStatus(), metadataResponseEntity);
			}

		} else {
			String getDoiEntity = getResp.readEntity(String.class);
			LOG.error("Problem with get DOI " + doi + " " + getResp.getStatusInfo() + getResp.getStatus() + " -- "
					+ getDoiEntity);
			throw new DoxiException(getResp.getStatus(), getDoiEntity);
		}
	}

	/**
	 * creates or updates metadata for a specific DOI
	 * 
	 * @param metadataXml
	 * @return Response of the Datacite service
	 */
	private Response createOrUpdateMetadata(String metadataXml) {
		return dataciteTarget.path("metadata").request(MediaType.TEXT_PLAIN_TYPE).post(Entity.xml(metadataXml));
	}

	/**
	 * creates or updates an URL for a specific DOI
	 * 
	 * @param doiAndUrl
	 * @return Response of the Datacite service
	 */
	private Response createOrUpdateUrl(String doiAndUrl) {
		return dataciteTarget.path("doi").request(MediaType.TEXT_PLAIN_TYPE).post(Entity.text(doiAndUrl));
	}

	/**
	 * replace DOI in metadata XML
	 * 
	 * @param metadataXml
	 * @param doi
	 * @return metadata XML
	 * @throws TransformerConfigurationException
	 * @throws TransformerException
	 */
	private String replaceDOIIdentifierInMetadataXml(String metadataXml, String doi) throws DoxiException {

		StringWriter writer = new StringWriter();

		try {
			TransformerFactory transFact = new TransformerFactoryImpl();
			InputStream stylesheet = DataciteAPIController.class.getResourceAsStream("/replace-doi.xsl");
			Transformer trans = transFact.newTransformer(new StreamSource(stylesheet));
			trans.setParameter("doi", doi);
			trans.transform(new StreamSource(new StringReader(metadataXml)), new StreamResult(writer));
		} catch (TerminationException e) {
			LOG.error(
					"The DOI identifier tag in the provided metadata xml must either be empty or match the provided DOI from the URL.",
					e);
			throw new DoxiException(
					"The DOI identifier tag in the provided metadata xml must either be empty or match the provided DOI from the URL.",
					e);
		} catch (Exception e) {
			LOG.error("The provided metadata xml is not well-formed.", e);
			throw new DoxiException("The provided metadata xml is not well-formed.", e);
		}

		return writer.toString();
	}

	/**
	 * generates a new DOI based on BASE36
	 * 
	 * @return a not yet registered DOI
	 * @throws Exception
	 */
	private String generateDoi() throws DoiRegisterException {
		return getDoiPrefix() + getNextDoiSuffix();
	}

	public String getNextDoiSuffix() throws DoiRegisterException {
		EntityManager em = EMF.emf.createEntityManager();
		try {
			em.getTransaction().begin();

			Query query = em.createNativeQuery("SELECT value FROM unique_identifier WHERE id = 1 FOR UPDATE;");
			List<Long> results = query.getResultList();
			String doiSuffix = Long.toString(results.get(0), 36);
			Query updateQuery = em.createNativeQuery("UPDATE unique_identifier SET value  = value + 1 WHERE id = 1;");

			updateQuery.executeUpdate();
			em.getTransaction().commit();
			return doiSuffix;
		} catch (Exception e) {
			em.getTransaction().rollback();
			throw new DoiRegisterException("Problem generating DOI", e);
		} finally {
			em.close();
		}
	}

	/**
	 * gets the DOI prefix for the current user
	 * 
	 * @return
	 */
	// TODO DOI get prefix (including service ID) for current user from database
	private String getDoiPrefix() {
		DoxiUser currentUser = (DoxiUser) secContext.getUserPrincipal();
		return currentUser.getPrefix();
	}
	
	private boolean prefixAllowed(String doi)
	{
		DoxiUser currentUser = (DoxiUser) secContext.getUserPrincipal();
		boolean isAdmin = false;
		for(DoxiRole role : currentUser.getRoles())
		{
			isAdmin = "admin".contentEquals(role.getRole());
			if(isAdmin) break;
		}
		boolean prefixAllowed = doi.toUpperCase(Locale.ENGLISH).startsWith(currentUser.getPrefix().toUpperCase(Locale.ENGLISH));
		return isAdmin || prefixAllowed;
	}
}
