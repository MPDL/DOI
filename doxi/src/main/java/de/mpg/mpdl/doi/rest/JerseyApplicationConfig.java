package de.mpg.mpdl.doi.rest;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.security.DenyAll;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.ApplicationPath;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.glassfish.jersey.server.mvc.MvcFeature;
import org.glassfish.jersey.server.mvc.mustache.MustacheMvcFeature;
import org.glassfish.jersey.servlet.ServletContainer;

import de.mpg.mpdl.doi.controller.DataciteAPIController;
import de.mpg.mpdl.doi.controller.DoiControllerInterface;
import de.mpg.mpdl.doi.security.HttpBasicContainerRequestFilter;
import de.mpg.mpdl.doi.util.PropertyReader;

@ApplicationPath("rest")

public class JerseyApplicationConfig extends ResourceConfig {
	
	public static EntityManagerFactory emf;
	
	static{
		Map<String,String> jpaDBProperties = new HashMap<String, String>();
		jpaDBProperties.put("javax.persistence.jdbc.driver", PropertyReader.getProperty("doxi.jdbc.driver"));
		jpaDBProperties.put("javax.persistence.jdbc.url", PropertyReader.getProperty("doxi.jdbc.url"));
		jpaDBProperties.put("javax.persistence.jdbc.user", PropertyReader.getProperty("doxi.jdbc.user"));
		jpaDBProperties.put("javax.persistence.jdbc.password", PropertyReader.getProperty("doxi.jdbc.password"));
		
		emf = Persistence.createEntityManagerFactory("default", jpaDBProperties);
		
	}
	
	
	Logger logger = LogManager.getLogger();
	public JerseyApplicationConfig()
	{
	    //property("contextConfigLocation", "classpath:applicationContext.xml");
	    packages(true,"de.mpg.mpdl.doi");
		
		property(MustacheMvcFeature.TEMPLATE_BASE_PATH, "mustache");
		register(MustacheMvcFeature.class);
		register(MvcFeature.class);
		
		register(new AbstractBinder() {
			
			@Override
			protected void configure() {
				bind(DataciteAPIController.class).to(DoiControllerInterface.class);
				
			}
		});
		
		

	    
	    //register(HttpBasicContainerRequestFilter.class);
		registerInstances(new LoggingFilter(java.util.logging.Logger.getLogger("test"), true));
		
		register(RolesAllowedDynamicFeature.class);

		

		
		
		//register(SecurityConfig.class);
		//register(SecurityWebApplicationInitializer.class);
		
		//ApplicationContext appCon = ContextLoader.getCurrentWebApplicationContext();
		//logger.info(appCon);
		
		/*
		 * 
		packages(true,"de.mpg");
		
		 
		 
		register(DOIResource.class);
		register(DoiAlreadyExistsMapper.class);
		*/
		//packages(true,"de.mpg.mpdl.doi.rest");//.packages("de.mpg.mpdl.doi.security.spring.config");
		/*
		.register(DOIResource.class);
		
		//property("contextConfigLocation", )
		
		
		*/
	}
	
	public static void main(String[] args) throws Exception
	{

		HttpServer server = new HttpServer();
		NetworkListener listener = new NetworkListener("grizzly2", "localhost", 8081);
		server.addListener(listener);
		
		server.getServerConfiguration().addHttpHandler(
		        new StaticHttpHandler("src/main/webapp/resources/"), "/resources");
		
		WebappContext ctx = new WebappContext("ctx","/");       
		
		
		//If Java-config should be used, create a class SecurityWebApplicationInitializer extends AbstractSecurityWebApplicationInitializer
		//and a config and use the following method:
		//SecurityWebApplicationInitializer initializer = new SecurityWebApplicationInitializer();
		//initializer.onStartup(ctx);
		
		
		
//		 If XML-Config should be used use SpringWebApplicationInitializer from package jersey-spring 3, which does the following:
//		 ctx.addContextInitParameter("contextConfigLocation", "classpath:applicationContext.xml");
//		 ctx.addListener(ContextLoaderListener.class);
//		 ctx.addListener(RequestContextListener.class);

		
//		Register Jersey Servlet
		ctx.addServlet("de.mpg.mpdl.doi.rest.JerseyApplicationConfig", new ServletContainer(new JerseyApplicationConfig())).addMapping("/*");

		ctx.deploy(server);
		
		server.start();
		
		Thread.currentThread().join();
	}
}
