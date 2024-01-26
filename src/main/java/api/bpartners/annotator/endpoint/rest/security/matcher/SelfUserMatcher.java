package api.bpartners.annotator.endpoint.rest.security.matcher;

import api.bpartners.annotator.endpoint.rest.security.authentication.AuthenticatedResourceProvider;
import org.springframework.http.HttpMethod;

public class SelfUserMatcher extends SelfMatcher {
  public SelfUserMatcher(
      HttpMethod method, String antPattern, AuthenticatedResourceProvider authResourceProvider) {
    super(method, antPattern, authResourceProvider);
  }

  @Override
  protected String getAccessibleProtectedResourceId() {
    return authResourceProvider.getAuthenticatedUser().getId();
  }
}
