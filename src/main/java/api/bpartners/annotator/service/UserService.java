package api.bpartners.annotator.service;

import static java.util.stream.Collectors.toList;

import api.bpartners.annotator.endpoint.event.EventProducer;
import api.bpartners.annotator.endpoint.event.gen.UserTeamUpdated;
import api.bpartners.annotator.endpoint.event.gen.UserUpserted;
import api.bpartners.annotator.endpoint.rest.security.model.Role;
import api.bpartners.annotator.model.BoundedPageSize;
import api.bpartners.annotator.model.PageFromOne;
import api.bpartners.annotator.model.exception.NotFoundException;
import api.bpartners.annotator.repository.jpa.UserRepository;
import api.bpartners.annotator.repository.model.Team;
import api.bpartners.annotator.repository.model.User;
import java.util.List;
import java.util.Optional;
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
  private final EventProducer eventProducer;

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
        .team(new Team("f162d92b-1c76-4fad-ae3f-494b1759bc33", "admin"))
        .build();
  }

  public User getById(String id) {
    Optional<User> actual = repository.findById(id);
    if (actual.isPresent()) {
      return actual.get();
    }
    throw new NotFoundException("User with id: " + id + ", is not found.");
  }

  public List<User> fireEvents(List<User> users) {
    eventProducer.accept(users.stream().map(this::toTypedUser).collect(toList()));
    return users;
  }

  public User save(User user) {
    return repository.save(user);
  }

  private UserUpserted toTypedUser(User user) {
    return UserUpserted.builder().user(user).build();
  }

  public List<User> findAll(PageFromOne page, BoundedPageSize pageSize) {
    Pageable pageable = PageRequest.of(page.getValue() - 1, pageSize.getValue());
    return repository.findAll(pageable).toList();
  }

  public User updateUserTeam(User toUpdate) {
    eventProducer.accept(List.of(toUserTeamUpdatedType(toUpdate)));
    return repository.save(toUpdate);
  }

  private UserTeamUpdated toUserTeamUpdatedType(User user) {
    return UserTeamUpdated.builder()
        .group(user.getTeam().getName())
        .username(user.getEmail())
        .build();
  }
}
