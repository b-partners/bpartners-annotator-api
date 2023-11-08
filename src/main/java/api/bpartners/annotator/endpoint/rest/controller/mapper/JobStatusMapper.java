package api.bpartners.annotator.endpoint.rest.controller.mapper;

import api.bpartners.annotator.endpoint.rest.model.JobStatus;
import org.springframework.stereotype.Component;

import static api.bpartners.annotator.endpoint.rest.model.JobStatus.COMPLETED;
import static api.bpartners.annotator.endpoint.rest.model.JobStatus.FAILED;
import static api.bpartners.annotator.endpoint.rest.model.JobStatus.PENDING;
import static api.bpartners.annotator.endpoint.rest.model.JobStatus.READY;
import static api.bpartners.annotator.endpoint.rest.model.JobStatus.STARTED;

@Component
public class JobStatusMapper {
  public JobStatus toRest(api.bpartners.annotator.repository.model.enums.JobStatus domain) {
    return switch (domain) {
      case PENDING -> PENDING;
      case READY -> READY;
      case STARTED -> STARTED;
      case FAILED -> FAILED;
      case COMPLETED -> COMPLETED;
    };
  }

  public api.bpartners.annotator.repository.model.enums.JobStatus toDomain(JobStatus rest) {
    return switch (rest) {
      case PENDING -> api.bpartners.annotator.repository.model.enums.JobStatus.PENDING;
      case READY -> api.bpartners.annotator.repository.model.enums.JobStatus.READY;
      case STARTED -> api.bpartners.annotator.repository.model.enums.JobStatus.STARTED;
      case FAILED -> api.bpartners.annotator.repository.model.enums.JobStatus.FAILED;
      case COMPLETED -> api.bpartners.annotator.repository.model.enums.JobStatus.COMPLETED;
    };
  }
}
