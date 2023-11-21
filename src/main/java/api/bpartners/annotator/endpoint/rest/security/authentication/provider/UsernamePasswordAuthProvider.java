package api.bpartners.annotator.endpoint.rest.security.authentication.provider;

import api.bpartners.annotator.endpoint.rest.security.authentication.authenticator.UsernamePasswordAuthenticator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class UsernamePasswordAuthProvider extends AbstractUserDetailsAuthenticationProvider {
  private final UsernamePasswordAuthenticator authenticator;

  @Override
  protected void additionalAuthenticationChecks(
      UserDetails userDetails, UsernamePasswordAuthenticationToken token) {
    // nothing
  }

  @Override
  protected UserDetails retrieveUser(
      String username, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) {
    log.info("retrieving user");
    return authenticator.retrieveUser(username, usernamePasswordAuthenticationToken);
  }
}
