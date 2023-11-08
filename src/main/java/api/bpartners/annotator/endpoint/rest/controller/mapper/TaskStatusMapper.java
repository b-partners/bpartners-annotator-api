package api.bpartners.annotator.endpoint.rest.controller.mapper;

import api.bpartners.annotator.endpoint.rest.model.TaskStatus;
import org.springframework.stereotype.Component;

import static api.bpartners.annotator.endpoint.rest.model.TaskStatus.COMPLETED;
import static api.bpartners.annotator.endpoint.rest.model.TaskStatus.PENDING;
import static api.bpartners.annotator.endpoint.rest.model.TaskStatus.UNDER_COMPLETION;

@Component
public class TaskStatusMapper {
  public TaskStatus toRest(api.bpartners.annotator.repository.model.enums.TaskStatus domain) {
    return switch (domain) {
      case PENDING -> PENDING;
      case UNDER_COMPLETION -> UNDER_COMPLETION;
      case COMPLETED -> COMPLETED;
    };
  }

  public api.bpartners.annotator.repository.model.enums.TaskStatus toDomain(TaskStatus rest) {
    return switch (rest) {
      case PENDING -> api.bpartners.annotator.repository.model.enums.TaskStatus.PENDING;
      case UNDER_COMPLETION ->
          api.bpartners.annotator.repository.model.enums.TaskStatus.UNDER_COMPLETION;
      case COMPLETED -> api.bpartners.annotator.repository.model.enums.TaskStatus.COMPLETED;
    };
  }
}
