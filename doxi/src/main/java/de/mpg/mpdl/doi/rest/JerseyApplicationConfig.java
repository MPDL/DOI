package de.mpg.mpdl.doi.rest;

import javax.ws.rs.ApplicationPath;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/")
public class JerseyApplicationConfig extends ResourceConfig {

	Logger logger = LogManager.getLogger();
	public JerseyApplicationConfig()
	{
		
	    //property("contextConfigLocation", "classpath:applicationContext.xml");
	    packages(true,"de.mpg.mpdl.doi");
		registerInstances(new LoggingFilter(java.util.logging.Logger.getLogger("test"), true));
		
		
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
}
