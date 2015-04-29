package de.mpg.mpdl.doi.security;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name="roles")
public class DoxiRole {
	
	@Column(name="role")
	@Id
	private String role;
	
	
	@Id
	private String username;
	
	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}




}
