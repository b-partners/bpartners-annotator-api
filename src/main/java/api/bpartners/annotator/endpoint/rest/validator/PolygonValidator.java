package api.bpartners.annotator.endpoint.rest.validator;

import api.bpartners.annotator.endpoint.rest.model.Polygon;
import api.bpartners.annotator.model.exception.BadRequestException;
import java.util.function.Consumer;
import org.springframework.stereotype.Component;

@Component
public class PolygonValidator implements Consumer<Polygon> {

  @Override
  public void accept(Polygon polygon) {
    StringBuilder exceptionMessageBuilder = new StringBuilder();
    if (polygon.getPoints() == null) {
      exceptionMessageBuilder.append("polygon points attribute is mandatory");
    }
    String exceptionMessage = exceptionMessageBuilder.toString();
    if (!exceptionMessage.isBlank()) {
      throw new BadRequestException(exceptionMessage);
    }
  }
}
