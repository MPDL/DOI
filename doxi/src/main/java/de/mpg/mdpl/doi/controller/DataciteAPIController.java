package de.mpg.mdpl.doi.controller;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URLEncoder;

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

public class DataciteAPIController {

	private static Logger logger = LogManager.getLogger();
	WebTarget dataciteTarget;
	
	public DataciteAPIController()
	{
		ClientConfig clientConfig = new ClientConfig();
		HttpAuthenticationFeature authFeature = HttpAuthenticationFeature.basic(PropertyReader.getProperty("datacite.api.login.user"), PropertyReader.getProperty("datacite.api.login.password"));
		clientConfig.register(authFeature);
		Client client = ClientBuilder.newClient(clientConfig);
		this.dataciteTarget = client.target(PropertyReader.getProperty("datacite.api.url"));
	}
	
	
	public void createDOI(String doi, String url, String metadataXml) throws Exception
	{

		Response getResp = dataciteTarget.path("doi").path(doi).request().get();
		if(getResp.getStatus() == Response.Status.OK.getStatusCode() || getResp.getStatus() == Response.Status.NO_CONTENT.getStatusCode())
		{
			logger.error("DOI " + doi + " already exists: " + getResp.getStatusInfo() + getResp.getStatus() + " -- " + getResp.readEntity(String.class));
			return;
		}
		else if(getResp.getStatus() == Response.Status.NOT_FOUND.getStatusCode())
		{
			metadataXml = replaceDOIIdentifierInMetadataXml(metadataXml, doi);
			
			Response mdResp = dataciteTarget.path("metadata").request(MediaType.TEXT_PLAIN_TYPE).post(Entity.xml(metadataXml));
			
			if(mdResp.getStatus() == Response.Status.CREATED.getStatusCode())
			{
				logger.info("Metadata uploaded successfully" + mdResp.getStatusInfo() + mdResp.getStatus() + " -- " + mdResp.readEntity(String.class));
				
				
				String entity="doi=" + doi + "\nurl=" + url;
				Response doiResp = dataciteTarget.path("doi").request(MediaType.TEXT_PLAIN_TYPE).post(Entity.text(entity));
				

				if(doiResp.getStatus() == Response.Status.CREATED.getStatusCode())
				{
					logger.info("URL uploaded successfully " + doiResp.getStatusInfo() + doiResp.getStatus() + " -- " + doiResp.readEntity(String.class));
				}
				else
				{
					logger.error("Problem with url upload " + doiResp.getStatusInfo() + doiResp.getStatus() + " -- " + doiResp.readEntity(String.class));
				}
					
				
			}
			else
			{
				logger.error("Problem with metadata " + mdResp.getStatusInfo() + mdResp.getStatus() + " -- " + mdResp.readEntity(String.class));
			}
			
		}
		else
		{
			logger.error("Problem with get DOI " + doi + " "  + getResp.getStatusInfo() + getResp.getStatus() + " -- " + getResp.readEntity(String.class));
		}
		
		
		
		
		
	}
	
	
	
	public void updateUrl(String doi, String url)
	{
		String entity="doi=" + doi + "\nurl=" + url;
		Response doiResp = dataciteTarget.path("doi").request(MediaType.TEXT_PLAIN_TYPE).post(Entity.text(entity));
		
		if(doiResp.getStatus() == Response.Status.CREATED.getStatusCode())
		{
			logger.info("URL uploaded successfully " + doiResp.getStatusInfo() + doiResp.getStatus() + " -- " + doiResp.readEntity(String.class));
		}
		else
		{
			logger.error("Problem with url upload " + doiResp.getStatusInfo() + doiResp.getStatus() + " -- " + doiResp.readEntity(String.class));
		}
	}
	
	private String replaceDOIIdentifierInMetadataXml(String metadataXml, String doi) throws Exception
	{
		TransformerFactory transFact = new TransformerFactoryImpl();
		InputStream stylesheet = DataciteAPIController.class.getResourceAsStream("/replace-doi.xsl");
		Transformer trans = transFact.newTransformer(new StreamSource(stylesheet));
		trans.setParameter("doi", doi);
		
		StringWriter writer = new StringWriter();
		trans.transform(new StreamSource(new StringReader(metadataXml)), new StreamResult(writer));
		return writer.toString();
		
		/*
		Processor proc = new Processor(false);
		XdmNode node = proc.newDocumentBuilder().build(new StreamSource(new StringReader(metadataXml)));
		
		XQueryCompiler compiler = proc.newXQueryCompiler();
		compiler.setEncoding("UTF-8");
		String query = ""
		
		*/
		
		
	}
	
	
	
	
}
