package de.mpg.mpdl.doxi.security;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name = "roles")
public class DoxiRole {
  @Id
  @Column(name = "role", nullable = false)
  private String role;

  @Id
  @Column(name = "username", nullable = false)
  private String username;

  public DoxiRole() {
  }

  public DoxiRole(String role, String username) {
    this.role = role;
    this.username = username;
  }

  public String getRole() {
    return role;
  }

  public String getUsername() {
    return username;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @Override
  public String toString() {
    return "DoxiRole [role=" + role + ", username=" + username + "]";
  }
}
