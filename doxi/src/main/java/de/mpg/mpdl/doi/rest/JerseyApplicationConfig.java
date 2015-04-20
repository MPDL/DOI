package de.mpg.mpdl.doi.rest;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;

import de.mpg.mpdl.doi.rest.exceptionMapper.DoiAlreadyExistsMapper;
import de.mpg.mpdl.doi.security.spring.config.SecurityConfig;
import de.mpg.mpdl.doi.security.spring.config.SecurityWebApplicationInitializer;

@ApplicationPath("/")
public class JerseyApplicationConfig extends ResourceConfig {

	public JerseyApplicationConfig()
	{
		packages(true,"de.mpg.mpdl.doi.rest");
		/*
		packages(true,"de.mpg");
		
		 
		 
		register(DOIResource.class);
		register(DoiAlreadyExistsMapper.class);
		*/
		//packages(true,"de.mpg.mpdl.doi.rest");//.packages("de.mpg.mpdl.doi.security.spring.config");
		/*
		.register(DOIResource.class);
		
		//property("contextConfigLocation", )
		register(SecurityConfig.class);
		register(SecurityWebApplicationInitializer.class);
		*/
	}
}
