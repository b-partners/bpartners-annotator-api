package api.bpartners.annotator.service;

import api.bpartners.annotator.endpoint.rest.security.model.Role;
import api.bpartners.annotator.model.BoundedPageSize;
import api.bpartners.annotator.model.PageFromOne;
import api.bpartners.annotator.model.exception.NotFoundException;
import api.bpartners.annotator.repository.jpa.UserRepository;
import api.bpartners.annotator.repository.model.User;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

  public List<User> saveAll(List<User> users) {
    return repository.saveAll(users);
  }

  public List<User> findAll(PageFromOne page, BoundedPageSize pageSize) {
    Pageable pageable = PageRequest.of(page.getValue() - 1, pageSize.getValue());
    return repository.findAll(pageable).toList();
  }
}
