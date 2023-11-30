package api.bpartners.annotator.service;

import static api.bpartners.annotator.repository.model.enums.JobStatus.TO_REVIEW;
import static api.bpartners.annotator.repository.model.enums.TaskStatus.COMPLETED;
import static api.bpartners.annotator.repository.model.enums.TaskStatus.PENDING;
import static api.bpartners.annotator.repository.model.enums.TaskStatus.TO_CORRECT;
import static api.bpartners.annotator.repository.model.enums.TaskStatus.UNDER_COMPLETION;

import api.bpartners.annotator.model.BoundedPageSize;
import api.bpartners.annotator.model.PageFromOne;
import api.bpartners.annotator.model.exception.BadRequestException;
import api.bpartners.annotator.model.exception.NotFoundException;
import api.bpartners.annotator.repository.jpa.TaskRepository;
import api.bpartners.annotator.repository.model.Task;
import api.bpartners.annotator.repository.model.enums.TaskStatus;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TaskService {
  private final TaskRepository repository;
  private final JobService jobService;

  public List<Task> getAllByJob(String jobId, PageFromOne page, BoundedPageSize pageSize) {
    Pageable pageable = PageRequest.of(page.getValue() - 1, pageSize.getValue());
    return repository.findAllByJobId(jobId, pageable);
  }

  public Task getByJobIdAndId(String jobId, String id) {
    return repository
        .findByJobIdAndId(jobId, id)
        .orElseThrow(
            () ->
                new NotFoundException(
                    "Task identified by job.id = " + jobId + " and id = " + id + " not found"));
  }

  public Task update(String jobId, String id, Task task) {
    Task entity = getByJobIdAndId(jobId, id);
    checkTaskStatusTransition(entity, task);
    task.setFilename(entity.getFilename());
    task.setJob(entity.getJob());
    Task saved = repository.save(task);
    if (saved.getStatus() == COMPLETED) {
      boolean hasJobCompletedAllTasks =
          repository.existsByJobIdAndStatusNotIn(
              task.getJob().getId(), List.of(PENDING, UNDER_COMPLETION, TO_CORRECT));
      if (hasJobCompletedAllTasks) {
        jobService.updateJobStatus(task.getJob().getId(), TO_REVIEW);
      }
    }
    return saved;
  }

  public Task getAvailableTaskFromJob(String teamId, String jobId) {
    Optional<Task> optionalTask = repository.findFirstByJobIdAndStatus(jobId, PENDING);
    if (optionalTask.isEmpty()) {
      return null;
    }
    Task availableTask = optionalTask.get();
    availableTask.setStatus(UNDER_COMPLETION);
    return update(jobId, availableTask.getId(), availableTask);
  }

  public List<Task> createTasks(List<Task> tasks) {
    return repository.saveAll(tasks);
  }

  public TaskStatus checkTaskStatusTransition(Task currentTask, Task newTask) {
    TaskStatus current = currentTask.getStatus();
    TaskStatus next = newTask.getStatus();
    BadRequestException exception =
        new BadRequestException(String.format("illegal transition: %s -> %s", current, next));
    return switch (current) {
      case PENDING -> switch (next) {
        case PENDING, UNDER_COMPLETION -> next;
        case TO_CORRECT, COMPLETED -> throw exception;
      };
      case UNDER_COMPLETION -> switch (next) {
        case PENDING, TO_CORRECT, COMPLETED -> next;
        case UNDER_COMPLETION -> throw exception;
      };
      case TO_CORRECT -> switch (next) {
        case PENDING, UNDER_COMPLETION -> throw exception;
        case TO_CORRECT, COMPLETED -> next;
      };
      case COMPLETED -> switch (next) {
        case TO_CORRECT, COMPLETED -> next;
        case PENDING, UNDER_COMPLETION -> throw exception;
      };
    };
  }
}
