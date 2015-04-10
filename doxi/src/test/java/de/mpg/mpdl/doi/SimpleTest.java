package de.mpg.mpdl.doi;

import java.io.InputStream;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;

import de.mpg.mdpl.doi.MyResource;
import de.mpg.mdpl.doi.controller.DataciteAPIController;
import de.mpg.mdpl.doi.rest.DOIResource;

public class SimpleTest extends JerseyTest {
	 
	private Logger logger = LogManager.getLogger(SimpleTest.class);
 
    @Override
    protected Application configure() {
        return new ResourceConfig(DOIResource.class);
    }
 
    @Test
    public void test() throws Exception {
    	String testDoi = "10.15771/doxi2";
    	String url = "http://qa-pubman.mpdl.mpg.de/pubman/item/escidoc:2123284";
    	InputStream metadata = SimpleTest.class.getResourceAsStream("/doi_metadata.xml");
        
    	
    	Response result = target("doi").path(testDoi).queryParam("url", url).request().put(Entity.text(metadata));
        
    	Assert.assertEquals("200", result.getStatus());
    	
    	
    	//new DataciteAPIController().updateUrl(testDoi, url);
    }
}
