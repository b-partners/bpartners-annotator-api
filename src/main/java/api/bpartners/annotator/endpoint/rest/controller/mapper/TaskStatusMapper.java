package api.bpartners.annotator.endpoint.rest.controller.mapper;

import static api.bpartners.annotator.endpoint.rest.model.TaskStatus.COMPLETED;
import static api.bpartners.annotator.endpoint.rest.model.TaskStatus.PENDING;
import static api.bpartners.annotator.endpoint.rest.model.TaskStatus.TO_CORRECT;
import static api.bpartners.annotator.endpoint.rest.model.TaskStatus.TO_REVIEW;
import static api.bpartners.annotator.endpoint.rest.model.TaskStatus.UNDER_COMPLETION;

import api.bpartners.annotator.endpoint.rest.model.TaskStatus;
import api.bpartners.annotator.model.exception.ApiException;
import api.bpartners.annotator.model.exception.BadRequestException;
import org.springframework.stereotype.Component;

@Component
public class TaskStatusMapper {
  public TaskStatus toRest(api.bpartners.annotator.repository.model.enums.TaskStatus domain) {
    return switch (domain) {
      case PENDING -> PENDING;
      case UNDER_COMPLETION -> UNDER_COMPLETION;
      case TO_CORRECT -> TO_CORRECT;
      case TO_REVIEW -> TO_REVIEW;
      case COMPLETED -> COMPLETED;
      default ->
          throw new ApiException(
              ApiException.ExceptionType.SERVER_EXCEPTION,
              "unknown TaskStatus from server = " + domain);
    };
  }

  public api.bpartners.annotator.repository.model.enums.TaskStatus toDomain(TaskStatus rest) {
    if (rest == null) {
      return null;
    }
    return switch (rest) {
      case PENDING -> api.bpartners.annotator.repository.model.enums.TaskStatus.PENDING;
      case UNDER_COMPLETION ->
          api.bpartners.annotator.repository.model.enums.TaskStatus.UNDER_COMPLETION;
      case TO_CORRECT -> api.bpartners.annotator.repository.model.enums.TaskStatus.TO_CORRECT;
      case TO_REVIEW -> api.bpartners.annotator.repository.model.enums.TaskStatus.TO_REVIEW;
      case COMPLETED -> api.bpartners.annotator.repository.model.enums.TaskStatus.COMPLETED;
      default -> throw new BadRequestException("unknown TaskStatus = " + rest);
    };
  }
}
