package api.bpartners.annotator.endpoint.rest.security.matcher;

import api.bpartners.annotator.endpoint.rest.security.AuthenticatedResourceProvider;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.RequestMatcher;

@AllArgsConstructor
public abstract class SelfMatcher implements RequestMatcher {
  private static final Pattern SELFABLE_URI_PATTERN =
      // /resourceType/id/...
      Pattern.compile("/[^/]+/(?<id>[^/]+)(/.*)?");
  private static final String GROUP_NAME = "id";
  protected final HttpMethod method;
  protected final String antPattern;
  protected final AuthenticatedResourceProvider authResourceProvider;

  protected abstract String getAccessibleProtectedResourceId();

  protected String getId(HttpServletRequest request) {
    Matcher uriMatcher = SELFABLE_URI_PATTERN.matcher(request.getRequestURI());
    return uriMatcher.find() ? uriMatcher.group(GROUP_NAME) : null;
  }

  @Override
  public boolean matches(HttpServletRequest request) {
    if (Arrays.asList(authResourceProvider.getAuthenticatedUser().getRoles())
        .contains(Role.ADMIN)) {
      return true;
    }
    return Objects.equals(getId(request), getAccessibleProtectedResourceId());
  }
}
