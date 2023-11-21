package api.bpartners.annotator.endpoint.rest.security.authentication.authenticator;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Primary
@Component
@AllArgsConstructor
public class UsernamePasswordAuthenticatorFacade implements UsernamePasswordAuthenticator {
  private final BearerAuthenticator bearerAuthenticator;

  @Override
  public UserDetails retrieveUser(
      String username, UsernamePasswordAuthenticationToken authentication) {
    return bearerAuthenticator.retrieveUser(username, authentication);
  }
}
