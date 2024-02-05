package api.bpartners.annotator.repository.jpa;

import api.bpartners.annotator.repository.model.Team;
import api.bpartners.annotator.repository.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
  Optional<User> findByEmail(String email);

  List<User> findAllByTeam(Team team);
}
