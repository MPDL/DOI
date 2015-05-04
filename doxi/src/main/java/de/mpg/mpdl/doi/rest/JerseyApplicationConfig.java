package de.mpg.mpdl.doi.rest;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.security.DenyAll;
import javax.persistence.EntityManager;
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
import de.mpg.mpdl.doi.security.DoxiRole;
import de.mpg.mpdl.doi.security.DoxiUser;
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

		
		
		
		try {
			String createUser = PropertyReader.getProperty("doxi.admin.create");
			if("true".equals(createUser))
			{
				createAdminUser();
			}
			
		} catch (Exception e) {
			logger.error("Error while creating admin user", e);
		}
		
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
	
	
	private void createAdminUser() throws Exception
	{
		EntityManager em = emf.createEntityManager();
		
		String username = PropertyReader.getProperty("doxi.admin.user");
		
		
		DoxiUser alreadyExistsUser = em.find(DoxiUser.class, username);
		if(alreadyExistsUser==null)
		{
			DoxiUser adminUser = new DoxiUser();
			adminUser.setUsername(username);
			adminUser.setPassword(PropertyReader.getProperty("doxi.admin.password"));
			adminUser.setPrefix(PropertyReader.getProperty("doxi.admin.prefix"));
			DoxiRole role = new DoxiRole("admin" ,PropertyReader.getProperty("doxi.admin.user"));
			adminUser.getRoles().add(role);
			em.getTransaction().begin();
			em.persist(adminUser);
			em.getTransaction().commit();
			logger.info("Admin user " + username + " successfully created in database");
		}
		else
		{
			logger.warn("Admin user was not created, because it already exists in database");
		}
		
		
		
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
