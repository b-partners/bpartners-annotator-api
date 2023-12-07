package api.bpartners.annotator.repository.jpa;

import api.bpartners.annotator.repository.model.Task;
import api.bpartners.annotator.repository.model.enums.TaskStatus;
import java.util.Collection;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, String> {
  Optional<Task> findByJobIdAndId(String jobId, String id);

  Optional<Task> findFirstByJobIdAndStatusIn(String jobId, Collection<TaskStatus> status);

  boolean existsByJobIdAndStatusIn(String jobId, Collection<TaskStatus> taskStatuses);
}
