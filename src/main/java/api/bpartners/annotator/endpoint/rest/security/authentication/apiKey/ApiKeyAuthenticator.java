package api.bpartners.annotator.endpoint.rest.security.authentication.apiKey;

import static java.util.Optional.ofNullable;

import api.bpartners.annotator.endpoint.rest.security.authentication.UsernamePasswordAuthenticator;
import api.bpartners.annotator.endpoint.rest.security.model.Principal;
import api.bpartners.annotator.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
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
  private final Map<String, User> admins;
  private final UserService userService;

  public ApiKeyAuthenticator(
      UserService userService, ObjectMapper om, @Value("${ADMINS}") String adminsAsString)
      throws JsonProcessingException {
    this.userService = userService;
    this.admins = om.readValue(adminsAsString, new TypeReference<>() {});
  }

  @Override
  public UserDetails retrieveUser(
      String username, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken)
      throws AuthenticationException {
    String apiKey = getApiKeyFromHeader(usernamePasswordAuthenticationToken);
    User authenticatedByApiKey = getByApiKey(apiKey);
    var authenticatedInternalUser = userService.findByEmail(authenticatedByApiKey.email());
    return new Principal(authenticatedInternalUser, apiKey);
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

  private User getByApiKey(String apiKey) {
    return ofNullable(admins.get(apiKey))
        .orElseThrow(() -> new UsernameNotFoundException("No user found with apiKey = " + apiKey));
  }
}
