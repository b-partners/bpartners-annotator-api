package api.bpartners.annotator.endpoint.rest.validator;

import api.bpartners.annotator.endpoint.rest.model.AnnotationBatchReview;
import api.bpartners.annotator.model.exception.BadRequestException;
import java.util.function.BiConsumer;
import org.springframework.stereotype.Component;

@Component
public class AnnotationReviewValidator implements BiConsumer<String, AnnotationBatchReview> {
  @Override
  public void accept(String pathVariableReviewId, AnnotationBatchReview annotationReview) {
    StringBuilder exceptionMessageBuilder = new StringBuilder();
    if (!pathVariableReviewId.equals(annotationReview.getId())) {
      exceptionMessageBuilder.append("not matching ID on pathVariable and requestBody payload.");
    }
    String exceptionMessage = exceptionMessageBuilder.toString();
    if (!exceptionMessage.isBlank()) {
      throw new BadRequestException(exceptionMessage);
    }
  }
}
