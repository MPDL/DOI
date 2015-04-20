package de.mpg.mpdl.doi;

import java.io.InputStream;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.ServletDeploymentContext;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.web.context.ContextLoaderListener;

import de.mpg.mpdl.doi.rest.JerseyApplicationConfig;

public class SimpleTest extends JerseyTest {

	private Logger logger = LogManager.getLogger(SimpleTest.class);

	private String testDoi = "10.15771/doxi5";
	private String url = "http://qa-pubman.mpdl.mpg.de/pubman/item/escidoc:2123284";
	private InputStream metadata = SimpleTest.class
			.getResourceAsStream("/doi_metadata.xml");
	private InputStream updatedMetadata = SimpleTest.class
			.getResourceAsStream("/doi_update_metadata.xml");

	
	
	
	@Override
	protected Application configure() {
		
		
		return new JerseyApplicationConfig();
		
		
		//return new ResourceConfig(DOIResource.class, DoxiExceptionMapper.class, DoiAlreadyExistsMapper.class, ExceptionMapper.class, SecurityConfig.class, SecurityWebApplicationInitializer.class);
	}
	/*
	@Override
    protected DeploymentContext configureDeployment(){
        return ServletDeploymentContext
                .forServlet(new ServletContainer(new JerseyApplicationConfig()))
                .addListener(ContextLoaderListener.class)
                .contextParam("contextConfigLocation", "classpath:applicationContext.xml")
                .build();
    }
    */

	@Test
	public void testGetDoiList() throws Exception {

		logger.info("--------------------- STARTING get DOI-List test ---------------------");
		Response result = target("doi").request().get();
		logger.info("Status: " + result.getStatus() + " expected 200");
		logger.info("Message: " + result.getEntityTag().getValue());
		logger.info("List: " + result.readEntity(String.class));
		Assert.assertEquals(200, result.getStatus());

		// new DataciteAPIController().updateUrl(testDoi, url);
		logger.info("--------------------- FINISHED get DOI-List test ---------------------");
	}

	@Test
	public void testCreateDoi() throws Exception {
		logger.info("--------------------- STARTING create DOI test ---------------------");
		Response result = target("doi").path(testDoi).queryParam("url", url)
				.request().put(Entity.xml(metadata));
		logger.info("Status: " + result.getStatus() + " expected 200");
		logger.info("Message: " + result.getEntityTag().getValue());
		Assert.assertEquals(201, result.getStatus());

		// new DataciteAPIController().updateUrl(testDoi, url);
		logger.info("--------------------- FINISHED create DOI test ---------------------");
	}

	@Test
	public void testUpdateMd() throws Exception {
		logger.info("--------------------- STARTING update DOI test ---------------------");
		Response result = target("doi").path(testDoi).queryParam("url", url).request()
				.post(Entity.xml(updatedMetadata));
		logger.info("Status: " + result.getStatus() + " expected 200\nEntity: " + result.readEntity(String.class));
		Assert.assertEquals(201, result.getStatus());

		// new DataciteAPIController().updateUrl(testDoi, url);
		logger.info("--------------------- FINISHED update DOI test ---------------------");
	}

	@Test
	public void testGetDoi() throws Exception {
		logger.info("--------------------- STARTING get DOI test ---------------------");
		Response result = target("doi").path(testDoi).queryParam("url", url)
				.request().get();
		logger.info("Status: " + result.getStatus() + " expected 200");
		logger.info("Message: " + result.getEntityTag().getValue());
		logger.info("Message: " + result.readEntity(String.class));
		Assert.assertEquals(200, result.getStatus());

		// new DataciteAPIController().updateUrl(testDoi, url);
		logger.info("--------------------- FINISHED get DOI test ---------------------");
	}
	
	@Test
	public void testException()
	{
		Response result = target("doi").path("test").request().get();
		logger.info("Exception " + result.readEntity(String.class) +result.getStatus());
		
		Response result2 = target("doi").path("test2").request().get();
		logger.info("Exception " + result2.readEntity(String.class) +result2.getStatus());
	}
}
