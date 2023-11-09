package api.bpartners.annotator.endpoint.rest.security;

import api.bpartners.annotator.endpoint.rest.security.cognito.CognitoComponent;
import api.bpartners.annotator.endpoint.rest.security.model.Principal;
import api.bpartners.annotator.repository.model.User;
import api.bpartners.annotator.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class AuthProvider extends AbstractUserDetailsAuthenticationProvider {
  public static final String BEARER_PREFIX = "Bearer ";
  private final CognitoComponent cognitoComponent;
  private final UserService userService;

  @Override
  protected void additionalAuthenticationChecks(
      UserDetails userDetails, UsernamePasswordAuthenticationToken token) {
    // nothing
  }

  @Override
  protected UserDetails retrieveUser(
      String username, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) {
    log.info("retrieving user");
    String bearer = getBearerFromHeader(usernamePasswordAuthenticationToken);
    if (bearer == null) {
      throw new UsernameNotFoundException("Bad credentials"); // NOSONAR
    }
    String email = cognitoComponent.getEmailByToken(bearer);
    if (email == null) {
      throw new UsernameNotFoundException("Bad credentials"); // NOSONAR
    }
    User user = userService.findByEmail(email);
    return new Principal(user, bearer);
  }

  private String getBearerFromHeader(
      UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) {
    Object tokenObject = usernamePasswordAuthenticationToken.getCredentials();
    if (!(tokenObject instanceof String) || !((String) tokenObject).startsWith(BEARER_PREFIX)) {
      return null;
    }
    return ((String) tokenObject).substring(BEARER_PREFIX.length()).trim();
  }
}
