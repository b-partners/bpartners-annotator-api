package api.bpartners.annotator.endpoint.rest.validator;

import api.bpartners.annotator.endpoint.rest.model.AnnotatedTask;
import api.bpartners.annotator.endpoint.rest.model.AnnotationBatch;
import api.bpartners.annotator.model.exception.BadRequestException;
import java.util.List;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AnnotatedTasksValidator implements Consumer<List<AnnotatedTask>> {
  private final AnnotationValidator annotationValidator;

  @Override
  public void accept(List<AnnotatedTask> annotatedTasks) {
    StringBuilder exceptionMessageBuilder = new StringBuilder();

    annotatedTasks.forEach(annotatedTask -> accept(exceptionMessageBuilder, annotatedTask));

    if (!exceptionMessageBuilder.isEmpty()) {
      throw new BadRequestException(exceptionMessageBuilder.toString());
    }
  }

  private void accept(StringBuilder stringBuilder, AnnotatedTask annotatedTask) {
    if (annotatedTask.getFilename() == null) {
      stringBuilder.append("task.filename is mandatory");
    }
    if (annotatedTask.getAnnotatorId() == null) {
      stringBuilder.append("task.annotatorId is mandatory");
    }
    AnnotationBatch annotationBatch = annotatedTask.getAnnotationBatch();
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
