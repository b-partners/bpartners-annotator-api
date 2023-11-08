package api.bpartners.annotator.repository.jpa;

import api.bpartners.annotator.repository.model.Job;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, String> {
  List<Job> findAllByTeamId(String teamId);
  Optional<Job> findByTeamIdAndId(String teamId, String id);
}
