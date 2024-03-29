package api.bpartners.annotator.repository.jpa;

import api.bpartners.annotator.repository.model.Job;
import api.bpartners.annotator.repository.model.enums.JobStatus;
import java.util.Collection;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, String> {
  Optional<Job> findByTeamIdAndIdAndStatusIn(
      String teamId, String id, Collection<JobStatus> statuses);
}
