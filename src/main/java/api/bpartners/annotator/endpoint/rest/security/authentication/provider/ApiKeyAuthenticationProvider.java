package api.bpartners.annotator.endpoint.rest.security.authentication.provider;

import api.bpartners.annotator.endpoint.rest.security.authentication.authenticator.ApiKeyAuthenticator;
import api.bpartners.annotator.endpoint.rest.security.authentication.model.ApiKeyAuthenticationToken;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@AllArgsConstructor
public class ApiKeyAuthenticationProvider implements AuthenticationProvider {
  public static final String API_KEY_HEADER = "x-api-key";
  private final ApiKeyAuthenticator authenticator;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    Assert.isInstanceOf(
        ApiKeyAuthenticationToken.class,
        authentication,
        "ApiKeyAuthentication token is the only class supported");
    ApiKeyAuthenticationToken token = (ApiKeyAuthenticationToken) authentication;
    return createSuccessAuthentication(
        authentication, authenticator.retrieveUser(token), token.getApiKey());
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return ApiKeyAuthenticationToken.class.isAssignableFrom(authentication);
  }

  protected Authentication createSuccessAuthentication(
      Authentication authentication, Object principal, String apiKey) {
    // Ensure we return the original credentials the user supplied,
    // so subsequent attempts are successful even with encoded passwords.
    // Also ensure we return the original getDetails(), so that future
    // authentication events after cache expiry contain the details
    ApiKeyAuthenticationToken result = ApiKeyAuthenticationToken.authenticated(principal, apiKey);
    result.setDetails(authentication.getDetails());
    return result;
  }
}
