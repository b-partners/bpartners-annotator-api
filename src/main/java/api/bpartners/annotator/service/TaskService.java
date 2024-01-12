package api.bpartners.annotator.service;

import static api.bpartners.annotator.repository.model.enums.TaskStatus.COMPLETED;
import static api.bpartners.annotator.repository.model.enums.TaskStatus.PENDING;
import static api.bpartners.annotator.repository.model.enums.TaskStatus.TO_CORRECT;
import static api.bpartners.annotator.repository.model.enums.TaskStatus.TO_REVIEW;
import static api.bpartners.annotator.repository.model.enums.TaskStatus.UNDER_COMPLETION;

import api.bpartners.annotator.model.BoundedPageSize;
import api.bpartners.annotator.model.PageFromOne;
import api.bpartners.annotator.model.exception.NotFoundException;
import api.bpartners.annotator.repository.dao.TaskDao;
import api.bpartners.annotator.repository.jpa.TaskRepository;
import api.bpartners.annotator.repository.model.Task;
import api.bpartners.annotator.repository.model.enums.TaskStatus;
import api.bpartners.annotator.service.validator.TaskUpdateValidator;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TaskService {
  public static final List<TaskStatus> NOT_COMPLETED_TASK_STATUSES =
      List.of(PENDING, UNDER_COMPLETION, TO_CORRECT);
  private final TaskRepository repository;
  private final JobService jobService;
  private final TaskDao taskDao;
  private final TaskUpdateValidator updateValidator;

  public List<Task> getAllByJobAndStatus(
      String jobId, TaskStatus status, String userId, PageFromOne page, BoundedPageSize pageSize) {
    Pageable pageable = PageRequest.of(page.getValue() - 1, pageSize.getValue());
    return taskDao.findAllByJobIdAndStatusAndUserId(jobId, status, userId, pageable);
  }

  public Task getByJobIdAndId(String jobId, String id) {
    return repository
        .findByJobIdAndId(jobId, id)
        .orElseThrow(
            () ->
                new NotFoundException(
                    "Task identified by job.id = " + jobId + " and id = " + id + " not found"));
  }

  public Task getById(String id) {
    return repository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Task identified by id = " + id + " not found."));
  }

  public Task update(String jobId, String id, Task task) {
    Task entity = getByJobIdAndId(jobId, id);
    updateValidator.accept(entity, task);
    task.setFilename(entity.getFilename());
    task.setJob(entity.getJob());
    Task saved = repository.save(task);
    if (saved.getStatus() == COMPLETED) {
      boolean doesJobHaveNotCompletedTasks =
          repository.existsByJobIdAndStatusIn(task.getJob().getId(), NOT_COMPLETED_TASK_STATUSES);
      if (!doesJobHaveNotCompletedTasks) {
        task.setJob(jobService.setToReview(jobId));
      }
    }
    return saved;
  }

  private Task updateStatus(String taskId, TaskStatus taskStatus) {
    Task persisted = getById(taskId);
    persisted.setStatus(taskStatus);
    return update(persisted.getJob().getId(), taskId, persisted);
  }

  public Task complete(String taskId) {
    return updateStatus(taskId, COMPLETED);
  }

  public Task setToReview(String taskId) {
    return updateStatus(taskId, TO_REVIEW);
  }

  public Task reject(String taskId) {
    return updateStatus(taskId, TO_CORRECT);
  }

  public Task getAvailableTaskFromJobOrJobAndUserId(String teamId, String jobId, String userId) {
    Optional<Task> optionalTask = taskDao.findAvailableTaskFromJobOrJobAndUserId(jobId, userId);
    if (optionalTask.isEmpty()) {
      return null;
    }
    Task availableTask = optionalTask.get();
    if (availableTask.getStatus() == PENDING) {
      availableTask.setStatus(UNDER_COMPLETION);
      availableTask.setUserId(userId);
    }
    return update(jobId, availableTask.getId(), availableTask);
  }

  public List<Task> createTasks(List<Task> tasks) {
    return repository.saveAll(tasks);
  }
}
