package api.bpartners.annotator.endpoint.rest.security.authentication;

import api.bpartners.annotator.endpoint.rest.security.authentication.apiKey.ApiKeyAuthenticator;
import api.bpartners.annotator.endpoint.rest.security.authentication.bearer.BearerAuthenticator;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Primary
@Component
@AllArgsConstructor
public class UsernamePasswordAuthenticatorFacade implements UsernamePasswordAuthenticator {
  private final BearerAuthenticator bearerAuthenticator;
  private final ApiKeyAuthenticator apiKeyAuthenticator;

  @Override
  public UserDetails retrieveUser(
      String username, UsernamePasswordAuthenticationToken authentication) {
    try {
      return bearerAuthenticator.retrieveUser(username, authentication);
    } catch (AuthenticationException ignored) {
      return apiKeyAuthenticator.retrieveUser(username, authentication);
    }
  }
}
