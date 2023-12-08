package api.bpartners.annotator.endpoint.rest.validator;

import api.bpartners.annotator.endpoint.rest.model.Annotation;
import api.bpartners.annotator.model.exception.BadRequestException;
import java.util.function.Consumer;
import org.springframework.stereotype.Component;

@Component
public class AnnotationValidator implements Consumer<Annotation> {
  @Override
  public void accept(Annotation annotation) {
    StringBuilder exceptionMessageBuilder = new StringBuilder();
    if (annotation.getLabel() == null) {
      exceptionMessageBuilder.append("label is mandatory");
    }
    if (annotation.getUserId() == null) {
      exceptionMessageBuilder.append("userId is mandatory");
    }
    if (annotation.getTaskId() == null) {
      exceptionMessageBuilder.append("taskId is mandatory");
    }
    if (annotation.getPolygon() == null) {
      exceptionMessageBuilder.append("polygon is mandatory");
    }
    String exceptionMessage = exceptionMessageBuilder.toString();
    if (!exceptionMessage.isBlank()) {
      throw new BadRequestException(exceptionMessage);
    }
  }
}
