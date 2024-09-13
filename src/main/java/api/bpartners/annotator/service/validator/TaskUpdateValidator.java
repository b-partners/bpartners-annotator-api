package api.bpartners.annotator.service.validator;

import static api.bpartners.annotator.repository.model.enums.TaskStatus.COMPLETED;
import static api.bpartners.annotator.repository.model.enums.TaskStatus.PENDING;
import static api.bpartners.annotator.repository.model.enums.TaskStatus.TO_CORRECT;
import static api.bpartners.annotator.repository.model.enums.TaskStatus.UNDER_COMPLETION;

import api.bpartners.annotator.model.exception.BadRequestException;
import api.bpartners.annotator.repository.model.Task;
import api.bpartners.annotator.repository.model.enums.TaskStatus;
import java.util.function.BiConsumer;
import org.springframework.stereotype.Component;

@Component
public class TaskUpdateValidator implements BiConsumer<Task, Task> {
  @Override
  public void accept(Task entity, Task update) {
    StringBuilder exceptionMessageBuilder = new StringBuilder();
    TaskStatus updatedTaskStatus = update.getStatus();
    if (UNDER_COMPLETION.equals(updatedTaskStatus)
        || TO_CORRECT.equals(updatedTaskStatus)
        || update.isCompleted()
        || update.isToReview()) {
      if (update.getUserId() == null) {
        exceptionMessageBuilder
            .append("userId is mandatory in order to update a task status to ")
            .append(updatedTaskStatus);
      }
    }
    if (PENDING.equals(updatedTaskStatus)) {
      if (update.getUserId() != null) {
        exceptionMessageBuilder
            .append("userId must be null in order to update a task status to ")
            .append(updatedTaskStatus);
      }
    }
    try {
      checkTaskStatusTransition(entity, update);
    } catch (BadRequestException e) {
      exceptionMessageBuilder.append(e.getMessage());
    }
    String exceptionMessage = exceptionMessageBuilder.toString();
    if (!exceptionMessage.isBlank()) {
      throw new BadRequestException(exceptionMessage);
    }
  }

  public TaskStatus checkTaskStatusTransition(Task currentTask, Task newTask) {
    TaskStatus current = currentTask.getStatus();
    TaskStatus next = newTask.getStatus();
    BadRequestException exception =
        new BadRequestException(String.format("illegal transition: %s -> %s", current, next));
    return switch (current) {
      case PENDING ->
          switch (next) {
            case PENDING, UNDER_COMPLETION -> next;
            case TO_CORRECT, TO_REVIEW, COMPLETED -> throw exception;
          };
      case UNDER_COMPLETION ->
          switch (next) {
            case PENDING, UNDER_COMPLETION, TO_REVIEW -> next;
            case TO_CORRECT, COMPLETED -> throw exception;
          };
      case TO_CORRECT ->
          switch (next) {
            case PENDING, UNDER_COMPLETION, COMPLETED -> throw exception;
            case TO_CORRECT, TO_REVIEW -> next;
          };
      case TO_REVIEW ->
          switch (next) {
            case TO_CORRECT, TO_REVIEW, COMPLETED -> next;
            case PENDING, UNDER_COMPLETION -> throw exception;
          };
      case COMPLETED ->
          switch (next) {
            case COMPLETED, TO_CORRECT -> next;
            case PENDING, UNDER_COMPLETION, TO_REVIEW -> throw exception;
          };
    };
  }
}
