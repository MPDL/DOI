package de.mpg.mpdl.doi;

import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;

import de.mpg.mdpl.doi.MyResource;

public class SimpleTest extends JerseyTest {
	 

 
    @Override
    protected Application configure() {
        return new ResourceConfig(MyResource.class);
    }
 
    @Test
    public void test() {
        final String hello = target("myresource").request().get(String.class);
        Assert.assertEquals("Got it!", hello);
    }
}
