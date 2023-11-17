package api.bpartners.annotator.repository.jpa;

import api.bpartners.annotator.repository.model.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, String> {
  Page<Team> findAll(Pageable pageable);
}
