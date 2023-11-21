package api.bpartners.annotator.endpoint.rest.security.authentication.manager;

import api.bpartners.annotator.endpoint.rest.security.authentication.provider.ApiKeyAuthenticationProvider;
import api.bpartners.annotator.endpoint.rest.security.authentication.provider.UsernamePasswordAuthProvider;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AuthenticationManager
    implements org.springframework.security.authentication.AuthenticationManager {
  private final UsernamePasswordAuthProvider usernamePasswordAuthProvider;
  private final ApiKeyAuthenticationProvider apiKeyAuthenticationProvider;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    return authentication instanceof UsernamePasswordAuthenticationToken
        ? usernamePasswordAuthProvider.authenticate(authentication)
        : apiKeyAuthenticationProvider.authenticate(authentication);
  }
}
