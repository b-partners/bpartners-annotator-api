package api.bpartners.annotator.endpoint.rest.security.principal;

import org.springframework.security.core.Authentication;

public interface PrincipalProvider {
  Authentication getAuthentication();
}
