package api.bpartners.annotator.endpoint.rest.controller.mapper;

import static api.bpartners.annotator.endpoint.rest.model.JobStatus.COMPLETED;
import static api.bpartners.annotator.endpoint.rest.model.JobStatus.FAILED;
import static api.bpartners.annotator.endpoint.rest.model.JobStatus.PENDING;
import static api.bpartners.annotator.endpoint.rest.model.JobStatus.READY;
import static api.bpartners.annotator.endpoint.rest.model.JobStatus.STARTED;
import static api.bpartners.annotator.endpoint.rest.model.JobStatus.TO_CORRECT;
import static api.bpartners.annotator.endpoint.rest.model.JobStatus.TO_REVIEW;

import api.bpartners.annotator.endpoint.rest.model.JobStatus;
import api.bpartners.annotator.model.exception.ApiException;
import api.bpartners.annotator.model.exception.BadRequestException;
import org.springframework.stereotype.Component;

@Component
public class JobStatusMapper {
  public JobStatus toRest(api.bpartners.annotator.repository.model.enums.JobStatus domain) {
    return switch (domain) {
      case PENDING -> PENDING;
      case READY -> READY;
      case STARTED -> STARTED;
      case TO_REVIEW -> TO_REVIEW;
      case TO_CORRECT -> TO_CORRECT;
      case FAILED -> FAILED;
      case COMPLETED -> COMPLETED;
      default ->
          throw new ApiException(
              ApiException.ExceptionType.SERVER_EXCEPTION,
              "unknown JobStatus from server = " + domain);
    };
  }

  public api.bpartners.annotator.repository.model.enums.JobStatus toDomain(JobStatus rest) {
    if (rest == null) {
      return null;
    }
    return switch (rest) {
      case PENDING -> api.bpartners.annotator.repository.model.enums.JobStatus.PENDING;
      case READY -> api.bpartners.annotator.repository.model.enums.JobStatus.READY;
      case STARTED -> api.bpartners.annotator.repository.model.enums.JobStatus.STARTED;
      case TO_REVIEW -> api.bpartners.annotator.repository.model.enums.JobStatus.TO_REVIEW;
      case TO_CORRECT -> api.bpartners.annotator.repository.model.enums.JobStatus.TO_CORRECT;
      case FAILED -> api.bpartners.annotator.repository.model.enums.JobStatus.FAILED;
      case COMPLETED -> api.bpartners.annotator.repository.model.enums.JobStatus.COMPLETED;
      default -> throw new BadRequestException("unknown JobStatus = " + rest);
    };
  }
}
