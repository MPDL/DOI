package de.mpg.mpdl.doxi.security;

import java.security.Principal;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

public class Authorizer implements SecurityContext {

  @Context
  UriInfo uriInfo;

  private Principal principal = null;

  public Authorizer(final DoxiUser user) {
    if (user != null) {
      principal = user;
    }
  }

  @Override
  public Principal getUserPrincipal() {
    return principal;
  }

  @Override
  public boolean isUserInRole(String role) {
    if (this.principal != null) {
      DoxiUser user = (DoxiUser) principal;

      for (DoxiRole doxiRole : user.getRoles()) {
        if (doxiRole.getRole() != null
            && (doxiRole.getRole().equals(role) || doxiRole.getRole().equals("admin"))) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public boolean isSecure() {
    return "https".equals(uriInfo.getRequestUri().getScheme());
  }

  @Override
  public String getAuthenticationScheme() {
    if (principal == null) {
      return null;
    }
    return SecurityContext.BASIC_AUTH;
  }

}
