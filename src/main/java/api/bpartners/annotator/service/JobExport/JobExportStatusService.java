package api.bpartners.annotator.service.JobExport;

import static api.bpartners.annotator.repository.model.Status.HealthStatus.FAILED;
import static api.bpartners.annotator.repository.model.Status.HealthStatus.SUCCEEDED;
import static api.bpartners.annotator.repository.model.Status.HealthStatus.UNKNOWN;
import static api.bpartners.annotator.repository.model.Status.ProgressionStatus.FINISHED;
import static api.bpartners.annotator.repository.model.Status.ProgressionStatus.PROCESSING;
import static java.time.Instant.now;

import api.bpartners.annotator.repository.jpa.JobExportStatusRepository;
import api.bpartners.annotator.repository.model.JobExport;
import api.bpartners.annotator.repository.model.JobExportStatus;
import api.bpartners.annotator.repository.model.Status.HealthStatus;
import api.bpartners.annotator.repository.model.Status.ProgressionStatus;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
class JobExportStatusService {
  private final JobExportStatusRepository repository;

  @Transactional
  public JobExport process(JobExport task) {
    return update(task, PROCESSING, UNKNOWN, null);
  }

  @Transactional
  public JobExport succeed(JobExport task) {
    return update(task, FINISHED, SUCCEEDED, null);
  }

  @Transactional
  public JobExport fail(JobExport task) {
    return update(task, FINISHED, FAILED, null);
  }

  private JobExport update(
      JobExport childTask, ProgressionStatus progression, HealthStatus health, String message) {
    var taskStatus =
        JobExportStatus.builder()
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
