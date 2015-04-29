package de.mpg.mpdl.doi.security;

import javax.persistence.Entity;

@Entity
public class DoxiRole {
	
	private String role;

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

}
