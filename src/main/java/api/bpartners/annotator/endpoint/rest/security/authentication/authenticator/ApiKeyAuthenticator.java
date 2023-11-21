package api.bpartners.annotator.endpoint.rest.security.authentication.authenticator;

import api.bpartners.annotator.endpoint.rest.security.authentication.model.ApiKeyAuthenticationToken;
import api.bpartners.annotator.endpoint.rest.security.model.Principal;
import api.bpartners.annotator.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class ApiKeyAuthenticator {
  private final UserService userService;
  private final String apiKey;

  public ApiKeyAuthenticator(UserService userService, @Value("${admin.api.key}") String apiKey) {
    this.userService = userService;
    this.apiKey = apiKey;
  }

  public UserDetails retrieveUser(ApiKeyAuthenticationToken token) {
    if (token == null || !this.apiKey.equals(token.getApiKey())) {
      throw new BadCredentialsException("Invalid API key");
    }
    return new Principal(userService.getAdmin(), token.getApiKey());
  }
}
