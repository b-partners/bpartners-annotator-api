package api.bpartners.annotator.endpoint.rest.security;

import api.bpartners.annotator.endpoint.rest.security.principal.PrincipalProvider;
import api.bpartners.annotator.repository.model.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AuthenticatedResourceProvider {
  public User getAuthenticatedUser() {
    return PrincipalProvider.getPrincipal().getUser();
  }
}
