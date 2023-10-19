package api.bpartners.annotator.service;

import api.bpartners.annotator.repository.jpa.TaskRepository;
import api.bpartners.annotator.repository.jpa.model.Task;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import static api.bpartners.annotator.repository.jpa.model.enums.TaskStatus.PENDING;
import static api.bpartners.annotator.repository.jpa.model.enums.TaskStatus.UNDER_COMPLETION;

@Service
@AllArgsConstructor
public class TaskService {
  private final TaskRepository repository;

  public List<Task> getAllByJob(String jobId) {
    return repository.findAllByJobId(jobId);
  }

  public Task getByJobIdAndId(String jobId, String id) {
    return repository.findByJobIdAndId(jobId, id).orElseThrow(() -> new RuntimeException(
        "Task identified by job.id = " + jobId + " and id = " + id + " not found"));
  }

  public Task update(String jobId, String id, Task task) {
    Task entity = getByJobIdAndId(jobId, id);
    task.setImageURI(entity.getImageURI());
    task.setS3ImageKey(entity.getS3ImageKey());
    return repository.save(task);
  }

  public Task getAvailableTaskFromJob(String teamId, String jobId) {
    Task availableTask = repository.findFirstByJobIdAndStatus(jobId, PENDING);
    if (availableTask == null) {
      return null;
    }
    availableTask.setStatus(UNDER_COMPLETION);
    //TODO: get image URI from S3 everytime we get available task
    return repository.save(availableTask);
  }
}
