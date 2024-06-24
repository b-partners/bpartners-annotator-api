package api.bpartners.annotator.service;

import static api.bpartners.annotator.repository.model.ExportTaskStatus.HealthStatus.FAILED;
import static api.bpartners.annotator.repository.model.ExportTaskStatus.HealthStatus.SUCCEEDED;
import static api.bpartners.annotator.repository.model.ExportTaskStatus.HealthStatus.UNKNOWN;
import static api.bpartners.annotator.repository.model.ExportTaskStatus.ProgressionStatus.FINISHED;
import static api.bpartners.annotator.repository.model.ExportTaskStatus.ProgressionStatus.PROCESSING;
import static java.time.Instant.now;

import api.bpartners.annotator.repository.jpa.ExportTaskStatusRepository;
import api.bpartners.annotator.repository.model.ExportTask;
import api.bpartners.annotator.repository.model.ExportTaskStatus;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ExportTaskStatusService {
  private final ExportTaskStatusRepository repository;

  @Transactional
  public ExportTask process(ExportTask task) {
    return update(task, PROCESSING, UNKNOWN, null);
  }

  @Transactional
  public ExportTask succeed(ExportTask task) {
    return update(task, FINISHED, SUCCEEDED, null);
  }

  @Transactional
  public ExportTask fail(ExportTask task) {
    return update(task, FINISHED, FAILED, null);
  }

  private ExportTask update(
      ExportTask childTask,
      ExportTaskStatus.ProgressionStatus progression,
      ExportTaskStatus.HealthStatus health,
      String message) {
    var taskStatus =
        ExportTaskStatus.builder()
            .creationDatetime(now())
            .progression(progression)
            .health(health)
            .message(message)
            .build();
    childTask.hasNewStatus(taskStatus);
    repository.save(taskStatus);
    return childTask;
  }
}
