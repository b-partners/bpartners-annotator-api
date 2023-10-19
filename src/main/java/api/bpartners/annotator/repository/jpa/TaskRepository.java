package api.bpartners.annotator.repository.jpa;

import api.bpartners.annotator.repository.jpa.model.Task;
import api.bpartners.annotator.repository.jpa.model.enums.TaskStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, String> {
  List<Task> findAllByJobId(String jobId);

  Optional<Task> findByJobIdAndId(String jobId, String id);
  Task findFirstByJobIdAndStatus(String jobId, TaskStatus status);
}
