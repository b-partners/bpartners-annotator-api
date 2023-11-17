package api.bpartners.annotator.repository.jpa;

import api.bpartners.annotator.repository.model.Task;
import api.bpartners.annotator.repository.model.enums.TaskStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, String> {
  List<Task> findAllByJobId(String jobId, Pageable pageable);

  Optional<Task> findByJobIdAndId(String jobId, String id);

  Task findFirstByJobIdAndStatus(String jobId, TaskStatus status);
}
