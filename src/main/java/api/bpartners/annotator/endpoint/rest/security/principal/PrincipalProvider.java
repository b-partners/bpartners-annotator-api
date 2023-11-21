package api.bpartners.annotator.endpoint.rest.security.principal;

import api.bpartners.annotator.endpoint.rest.security.model.Principal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public interface PrincipalProvider {
  Authentication getAuthentication();

  static Principal getPrincipal() {
    SecurityContext context = SecurityContextHolder.getContext();
    Authentication authentication = context.getAuthentication();
    return (Principal) authentication.getPrincipal();
  }
}
