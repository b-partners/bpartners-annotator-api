package api.bpartners.annotator.endpoint.rest.validator;

import api.bpartners.annotator.endpoint.rest.model.AnnotationBaseFields;
import api.bpartners.annotator.endpoint.rest.model.Polygon;
import api.bpartners.annotator.model.exception.BadRequestException;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CreateAnnotationValidator implements Consumer<AnnotationBaseFields> {
  private final PolygonValidator polygonValidator;

  @Override
  public void accept(AnnotationBaseFields annotation) {
    StringBuilder exceptionMessageBuilder = new StringBuilder();
    if (annotation.getLabel() == null) {
      exceptionMessageBuilder.append("label is mandatory");
    }
    if (annotation.getUserId() == null) {
      exceptionMessageBuilder.append("userId is mandatory");
    }
    Polygon polygon = annotation.getPolygon();
    if (polygon == null) {
      exceptionMessageBuilder.append("polygon is mandatory");
    } else {
      polygonValidator.accept(polygon);
    }
    String exceptionMessage = exceptionMessageBuilder.toString();
    if (!exceptionMessage.isBlank()) {
      throw new BadRequestException(exceptionMessage);
    }
  }
}
