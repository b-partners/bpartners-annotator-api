package api.bpartners.annotator.endpoint.rest.validator;

import api.bpartners.annotator.endpoint.rest.model.CrupdateJob;
import api.bpartners.annotator.model.exception.BadRequestException;
import java.util.Objects;
import java.util.function.BiConsumer;
import org.springframework.stereotype.Component;

@Component
public class CrupdateJobIdValidator implements BiConsumer<CrupdateJob, String> {

  @Override
  public void accept(CrupdateJob payload, String pathVariableId) {
    StringBuilder exceptionMessageBuilder = new StringBuilder();
    if (!Objects.equals(payload.getId(), pathVariableId)) {
      exceptionMessageBuilder.append("payload Id and pathVariable Id doesn't match.");
    }
    String exceptionMessage = exceptionMessageBuilder.toString();
    if (!exceptionMessage.isBlank()) {
      throw new BadRequestException(exceptionMessage);
    }
  }
}
