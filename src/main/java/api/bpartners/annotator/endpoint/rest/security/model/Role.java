package api.bpartners.annotator.endpoint.rest.security.model;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
  ANNOTATOR,
  ADMIN;

  public String getRole() {
    return name();
  }

  @Override
  public String getAuthority() {
    return "ROLE_" + getRole();
  }
}
