package de.mpg.mpdl.doi.security.spring.config;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

public class SecurityWebApplicationInitializer extends AbstractSecurityWebApplicationInitializer {
	
	public SecurityWebApplicationInitializer() {
		super(SecurityConfig.class);
	}

}
