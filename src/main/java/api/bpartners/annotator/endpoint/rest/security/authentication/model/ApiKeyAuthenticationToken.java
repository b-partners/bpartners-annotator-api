package api.bpartners.annotator.endpoint.rest.security.authentication.model;

import static api.bpartners.annotator.endpoint.rest.security.model.Role.ADMIN;

import java.util.Collection;
import java.util.List;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

@Getter
public class ApiKeyAuthenticationToken extends AbstractAuthenticationToken {
  private Object principal;
  private final String apiKey;

  public ApiKeyAuthenticationToken(
      Object principal, String apiKey, Collection<? extends GrantedAuthority> authorities) {
    super(authorities);
    this.principal = principal;
    this.apiKey = apiKey;
    super.setAuthenticated(true);
  }

  public ApiKeyAuthenticationToken(Object principal, String apiKey) {
    super(List.of(ADMIN));
    this.principal = principal;
    this.apiKey = apiKey;
    setAuthenticated(false);
  }

  @Override
  public Object getCredentials() {
    return null;
  }

  @Override
  public Object getPrincipal() {
    return this.principal;
  }

  public static ApiKeyAuthenticationToken authenticated(Object principal, String apiKey) {
    return new ApiKeyAuthenticationToken(principal, apiKey, List.of(ADMIN));
  }

  @Override
  public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
    Assert.isTrue(
        !isAuthenticated,
        "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list"
            + " instead");
    super.setAuthenticated(false);
  }
}
