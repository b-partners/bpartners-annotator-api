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
import api.bpartners.annotator.repository.model.Job;
import api.bpartners.annotator.repository.model.Task;
import api.bpartners.annotator.repository.model.User;
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
  private final UserService userService;

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
    task.setJob(jobService.refresh(entity.getJob().getId()));
    Task saved = repository.save(task);
    if (saved.isCompleted()) {
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
    Task updated = updateStatus(taskId, TO_CORRECT);
    Job refreshedJob = jobService.rejectForCorrection(updated.getJob().getId());
    updated.setJob(refreshedJob);
    return updated;
  }

  /*/!\ breaks the logic of Status update : not using updateStatus because it also needs userId*/
  public Task setToUnderCompletionByUserId(String userId, String taskId) {
    Task persisted = getById(taskId);
    persisted.setUserId(userId);
    persisted.setStatus(UNDER_COMPLETION);
    return update(persisted.getJob().getId(), persisted.getId(), persisted);
  }

  public Task getAvailableTaskFromJobOrJobAndUserIdOrJobAndExternalUsers(
      String teamId, String jobId, String userId) {
    List<String> geoJobsUsers = userService.getAllGeoJobsUsers().stream().map(User::getId).toList();
    Optional<Task> optionalTask =
        taskDao.findAvailableTaskFromJobOrJobAndUserIdOrJobAndExternalUserIds(
            jobId, userId, geoJobsUsers);

    if (optionalTask.isEmpty()) {
      return null;
    }
    Task availableTask = optionalTask.get();
    boolean isAnnotatedByGeojobs =
        geoJobsUsers.contains(availableTask.getUserId())
            && availableTask.getUserId() != null
            && !availableTask.getUserId().equals(userId);
    if (PENDING.equals(availableTask.getStatus()) || isAnnotatedByGeojobs) {
      // if the task is from GeoJobs, update the task to be our current user's property
      return setToUnderCompletionByUserId(userId, availableTask.getId());
    }

    return availableTask;
  }

  public List<Task> createTasks(List<Task> tasks) {
    return repository.saveAll(tasks);
  }

  public Task save(Task task) {
    return repository.save(task);
  }
}
