package api.bpartners.annotator.repository.jpa;

import api.bpartners.annotator.repository.model.Job;
import api.bpartners.annotator.repository.model.enums.JobStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, String> {
  List<Job> findAllByTeamIdAndStatus(String teamId, JobStatus status);

  Optional<Job> findByTeamIdAndId(String teamId, String id);
}
