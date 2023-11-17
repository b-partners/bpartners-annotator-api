package api.bpartners.annotator.service;

import api.bpartners.annotator.endpoint.rest.security.model.Role;
import api.bpartners.annotator.model.exception.NotFoundException;
import api.bpartners.annotator.repository.jpa.UserRepository;
import api.bpartners.annotator.repository.model.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {
  private final UserRepository repository;

  public User findByEmail(String email) {
    return repository
        .findByEmail(email)
        .orElseThrow(() -> new NotFoundException("User with User.email= " + email + " not found."));
  }

  public User getAdmin() {
    return User.builder()
        .id("f162d92b-1c76-4fad-ae3f-494b1759bc33")
        .email("contact@bpartners.app")
        .roles(new api.bpartners.annotator.endpoint.rest.security.model.Role[] {Role.ADMIN})
        .team(null)
        .build();
  }
}
