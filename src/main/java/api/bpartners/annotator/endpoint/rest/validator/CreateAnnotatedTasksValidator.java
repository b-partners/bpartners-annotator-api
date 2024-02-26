package api.bpartners.annotator.endpoint.rest.validator;

import api.bpartners.annotator.endpoint.rest.model.CreateAnnotatedTask;
import api.bpartners.annotator.model.exception.BadRequestException;
import java.util.List;
import java.util.function.BiConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CreateAnnotatedTasksValidator
    implements BiConsumer<List<CreateAnnotatedTask>, String> {
  private final int maxInsertableTasksNumber;
  private final CreateAnnotationValidator annotationValidator;

  public CreateAnnotatedTasksValidator(
      @Value("${tasks.insert.limit.max}") String maxInsertableTasksNumber,
      CreateAnnotationValidator annotationValidator) {
    this.maxInsertableTasksNumber = Integer.parseInt(maxInsertableTasksNumber);
    this.annotationValidator = annotationValidator;
  }

  @Override
  public void accept(List<CreateAnnotatedTask> annotatedTasks, String linkedJobId) {
    StringBuilder exceptionMessageBuilder = new StringBuilder();

    if (annotatedTasks.size() > maxInsertableTasksNumber) {
      throw new BadRequestException(
          "cannot add tasks to Job.Id = "
              + linkedJobId
              + " only "
              + maxInsertableTasksNumber
              + " tasks per save is supported.");
    }

    annotatedTasks.forEach(annotatedTask -> accept(exceptionMessageBuilder, annotatedTask));

    if (!exceptionMessageBuilder.isEmpty()) {
      throw new BadRequestException(exceptionMessageBuilder.toString());
    }
  }

  private void accept(StringBuilder stringBuilder, CreateAnnotatedTask annotatedTask) {
    if (annotatedTask.getId() == null) {
      stringBuilder.append("task.id is mandatory");
    }
    if (annotatedTask.getFilename() == null) {
      stringBuilder.append("task.filename is mandatory");
    }
    if (annotatedTask.getAnnotatorId() == null) {
      stringBuilder.append("task.annotatorId is mandatory");
    }
    var annotationBatch = annotatedTask.getAnnotationBatch();
    if (annotationBatch == null) {
      stringBuilder.append("annotationBatch is mandatory");
    } else {
      if (annotationBatch.getAnnotations() == null) {
        stringBuilder.append("annotationBatch.annotations are mandatory");
      } else {
        annotationBatch.getAnnotations().forEach(annotationValidator);
      }
    }
  }
}
