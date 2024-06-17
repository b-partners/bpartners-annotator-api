package api.bpartners.annotator.service.JobExport;

import static api.bpartners.annotator.repository.model.Status.HealthStatus.FAILED;
import static api.bpartners.annotator.repository.model.Status.HealthStatus.SUCCEEDED;
import static api.bpartners.annotator.repository.model.Status.HealthStatus.UNKNOWN;
import static api.bpartners.annotator.repository.model.Status.ProgressionStatus.FINISHED;
import static api.bpartners.annotator.repository.model.Status.ProgressionStatus.PROCESSING;
import static java.time.Instant.now;

import api.bpartners.annotator.repository.jpa.AnnotationBatchPageExportStatusRepository;
import api.bpartners.annotator.repository.model.AnnotationBatchPageExport;
import api.bpartners.annotator.repository.model.AnnotationBatchPageExportStatus;
import api.bpartners.annotator.repository.model.Status.HealthStatus;
import api.bpartners.annotator.repository.model.Status.ProgressionStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Component
class AnnotationBatchPageExportStatusService {
  private final AnnotationBatchPageExportStatusRepository repository;

  @Transactional
  public AnnotationBatchPageExport process(AnnotationBatchPageExport target) {
    return update(target, PROCESSING, UNKNOWN, null);
  }

  @Transactional
  public AnnotationBatchPageExport succeed(AnnotationBatchPageExport target) {
    return update(target, FINISHED, SUCCEEDED, null);
  }

  @Transactional
  public AnnotationBatchPageExport fail(AnnotationBatchPageExport target) {
    return update(target, FINISHED, FAILED, null);
  }

  private AnnotationBatchPageExport update(
      AnnotationBatchPageExport target,
      ProgressionStatus progression,
      HealthStatus health,
      String message) {
    var taskStatus =
        AnnotationBatchPageExportStatus.builder()
            .creationDatetime(now())
            .progression(progression)
            .health(health)
            .message(message)
            .build();
    target.hasNewStatus(taskStatus);
    repository.save(taskStatus);

    return target;
  }
}
