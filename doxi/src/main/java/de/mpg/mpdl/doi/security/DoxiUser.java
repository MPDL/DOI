package de.mpg.mpdl.doi.security;

import java.security.Principal;

import javax.persistence.Entity;

@Entity
public class DoxiUser implements Principal {

	
	private String username;
	
	private String password;
	
	private String email;
	
	private String prefix;
	
	

	@Override
	public String getName() {
		return username;
	}
	
	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getPrefix() {
		return prefix;
	}


	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}



}
