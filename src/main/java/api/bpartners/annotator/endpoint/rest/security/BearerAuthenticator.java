package api.bpartners.annotator.endpoint.rest.security;

import api.bpartners.annotator.endpoint.rest.security.cognito.CognitoComponent;
import api.bpartners.annotator.endpoint.rest.security.model.Principal;
import api.bpartners.annotator.repository.model.User;
import api.bpartners.annotator.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class BearerAuthenticator implements UsernamePasswordAuthenticator {
  public static final String BEARER_PREFIX = "Bearer ";

  private final UserService userService;
  private final CognitoComponent cognitoComponent;

  @Override
  public UserDetails retrieveUser(
      String username, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken)
      throws AuthenticationException {
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
