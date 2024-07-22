package api.bpartners.annotator.endpoint.rest.security;

import api.bpartners.annotator.endpoint.rest.security.model.Principal;
import api.bpartners.annotator.service.UserService;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class ApiKeyAuthenticator implements UsernamePasswordAuthenticator {
  public static final String API_KEY_HEADER = "x-api-key";
  private final String apiKey;

  public ApiKeyAuthenticator(@Value("${admin.api.key}") String apiKey, UserService userService) {
    this.apiKey = apiKey;
    this.userService = userService;
  }

  private final UserService userService;

  @Override
  public UserDetails retrieveUser(
      String username, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken)
      throws AuthenticationException {
    String apiKeyToVerify = getApiKeyFromHeader(usernamePasswordAuthenticationToken);
    if (!this.apiKey.equals(apiKeyToVerify)) {
      throw new UsernameNotFoundException("Invalid Api key");
    }
    return new Principal(userService.getAdmin(), apiKeyToVerify);
  }

  private String getApiKeyFromHeader(
      UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) {
    Object tokenObject = usernamePasswordAuthenticationToken.getCredentials();
    if (!(tokenObject instanceof String)
        || !Objects.equals(usernamePasswordAuthenticationToken.getName(), API_KEY_HEADER)) {
      return null;
    }
    return ((String) tokenObject);
  }
}
