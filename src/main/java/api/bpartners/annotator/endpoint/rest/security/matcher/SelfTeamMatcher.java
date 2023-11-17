package api.bpartners.annotator.endpoint.rest.security.matcher;

import api.bpartners.annotator.endpoint.rest.security.AuthenticatedResourceProvider;
import org.springframework.http.HttpMethod;

public class SelfTeamMatcher extends SelfMatcher {
  public SelfTeamMatcher(
      HttpMethod method, String antPattern, AuthenticatedResourceProvider authResourceProvider) {
    super(method, antPattern, authResourceProvider);
  }

  @Override
  protected String getAccessibleProtectedResourceId() {
    return authResourceProvider.getAuthenticatedUser().getTeam().getId();
  }
}
