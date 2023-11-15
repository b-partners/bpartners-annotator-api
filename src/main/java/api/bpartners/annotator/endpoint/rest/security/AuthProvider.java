package api.bpartners.annotator.endpoint.rest.security;

import api.bpartners.annotator.endpoint.rest.security.cognito.CognitoComponent;
import api.bpartners.annotator.endpoint.rest.security.model.Principal;
import api.bpartners.annotator.repository.model.User;
import api.bpartners.annotator.service.UserService;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuthProvider extends AbstractUserDetailsAuthenticationProvider {
  public static final String API_KEY_HEADER_NAME = "x-api-key";
  public static final String BEARER_PREFIX = "Bearer ";
  private final CognitoComponent cognitoComponent;
  private final UserService userService;
  private final String apiKey;

  public AuthProvider(CognitoComponent cognitoComponent,
                      UserService userService,
                      @Value("${admin.api.key}")
                      String apiKey) {
    this.cognitoComponent = cognitoComponent;
    this.userService = userService;
    this.apiKey = apiKey;
  }

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
    String apiKey = getApiKeyFromHeader(usernamePasswordAuthenticationToken);
    if (bearer != null || apiKey != null) {
      if (apiKey == null) {
        String email = cognitoComponent.getEmailByToken(bearer);
        if (email == null) {
          throw new UsernameNotFoundException("Bad credentials"); // NOSONAR
        }
        User user = userService.findByEmail(email);
        return new Principal(user, bearer);
      } else if (apiKey.equals(this.apiKey)) {
        return new Principal(userService.getAdmin(), apiKey);
      }
    }
    throw new UsernameNotFoundException("Bad credentials"); // NOSONAR
  }

  private String getBearerFromHeader(
      UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) {
    Object tokenObject = usernamePasswordAuthenticationToken.getCredentials();
    if (!(tokenObject instanceof String) || !((String) tokenObject).startsWith(BEARER_PREFIX)) {
      return null;
    }
    return ((String) tokenObject).substring(BEARER_PREFIX.length()).trim();
  }

  private String getApiKeyFromHeader(
      UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) {
    Object tokenObject = usernamePasswordAuthenticationToken.getCredentials();
    if (!(tokenObject instanceof String)
        || !Objects.equals(usernamePasswordAuthenticationToken.getName(), API_KEY_HEADER_NAME)) {
      return null;
    }
    return ((String) tokenObject);
  }

  public static Principal getPrincipal() {
    SecurityContext context = SecurityContextHolder.getContext();
    Authentication authentication = context.getAuthentication();
    return (Principal) authentication.getPrincipal();
  }
}
