package de.mpg.mpdl.doi.rest;

import javax.ws.rs.ApplicationPath;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import de.mpg.mpdl.doi.rest.exceptionMapper.DoiAlreadyExistsMapper;
import de.mpg.mpdl.doi.security.spring.config.SecurityConfig;
import de.mpg.mpdl.doi.security.spring.config.SecurityWebApplicationInitializer;

@ApplicationPath("/")
public class JerseyApplicationConfig extends ResourceConfig {

	Logger logger = LogManager.getLogger();
	public JerseyApplicationConfig()
	{
		packages(true,"de.mpg.mpdl.doi");
	    //property("contextConfigLocation", "classpath:applicationContext.xml");
		
		register(SecurityConfig.class);
		register(SecurityWebApplicationInitializer.class);
		
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
