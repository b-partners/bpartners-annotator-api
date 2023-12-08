package api.bpartners.annotator.endpoint.rest.validator;

import static api.bpartners.annotator.endpoint.rest.model.TaskStatus.COMPLETED;
import static api.bpartners.annotator.endpoint.rest.model.TaskStatus.PENDING;
import static api.bpartners.annotator.endpoint.rest.model.TaskStatus.UNDER_COMPLETION;

import api.bpartners.annotator.endpoint.rest.model.UpdateTask;
import api.bpartners.annotator.model.exception.BadRequestException;
import java.util.function.Consumer;
import org.springframework.stereotype.Component;

@Component
public class UpdateTaskValidator implements Consumer<UpdateTask> {

  @Override
  public void accept(UpdateTask updateTask) {
    // status transition is checked in service as this's class's purpose is to check for attributes
    // validity,
    // not their mergeability with the stored data
    StringBuilder exceptionMessageBuilder = new StringBuilder();
    if (UNDER_COMPLETION.equals(updateTask.getStatus())
        || COMPLETED.equals(updateTask.getStatus())) {
      if (updateTask.getUserId() == null) {
        exceptionMessageBuilder
            .append("userId is mandatory in order to update a task status to ")
            .append(updateTask.getStatus());
      }
    }

    if (PENDING.equals(updateTask.getStatus())) {
      if (updateTask.getUserId() != null) {
        exceptionMessageBuilder
            .append("userId must be null in order to update a task status to ")
            .append(updateTask.getStatus());
      }
    }
    String exceptionMessage = exceptionMessageBuilder.toString();
    if (!exceptionMessage.isBlank()) {
      throw new BadRequestException(exceptionMessage);
    }
  }
}
