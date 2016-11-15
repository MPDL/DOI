package de.mpg.mpdl.doxi.security;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

@Entity(name = "users")
@Cacheable(false)
public class DoxiUser implements Principal {
  @Id
  @Column(name = "username", nullable = false)
  private String username;

  @Column(name = "password", nullable = false)
  @Convert(converter = PasswordCryptoConverter.class)
  private String password;

  @Column(name = "email")
  private String email;

  @Column(name = "prefix", nullable = false)
  private String prefix;

  @OneToMany(cascade = CascadeType.PERSIST)
  @JoinColumn(name = "username", referencedColumnName = "username")
  private List<DoxiRole> roles = new ArrayList<DoxiRole>();

  public String getEmail() {
    return email;
  }

  @Override
  public String getName() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String getPrefix() {
    return prefix;
  }

  public List<DoxiRole> getRoles() {
    return roles;
  }

  public String getUsername() {
    return username;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public void setRoles(List<DoxiRole> roles) {
    this.roles = roles;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @Override
  public String toString() {
    return "DoxiUser [username=" + username + ", password=" + password + ", email=" + email
        + ", prefix=" + prefix + ", roles=" + roles + "]";
  }
}
