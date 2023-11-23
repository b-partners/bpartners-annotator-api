package api.bpartners.annotator.endpoint.rest.validator;

import api.bpartners.annotator.endpoint.rest.model.Label;
import api.bpartners.annotator.model.exception.BadRequestException;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class LabelValidator implements Consumer<Label> {
  private static final Pattern VALID_COLOR_PATTERN = Pattern.compile("^#[0-9a-fA-F]{6}$");

  @Override
  public void accept(Label label) {
    StringBuilder exceptionMessageBuilder = new StringBuilder();
    if (label.getId() == null) {
      exceptionMessageBuilder.append("Label id is mandatory.");
    }
    if (label.getName() == null) {
      exceptionMessageBuilder.append("Label name is mandatory.");
    }
    if (label.getColor() == null) {
      exceptionMessageBuilder.append("Label color is mandatory.");
    } else if (label.getColor() != null) {
      if (!VALID_COLOR_PATTERN.matcher(label.getColor()).matches()) {
        exceptionMessageBuilder
            .append("Label color: ")
            .append(label.getColor())
            .append(" does not follow regex ")
            .append(VALID_COLOR_PATTERN.pattern())
            .append(".");
      }
    }
    String exceptionMessage = exceptionMessageBuilder.toString();
    if (!exceptionMessage.isBlank()) {
      throw new BadRequestException(exceptionMessage);
    }
  }

  public void accept(List<Label> labels) {
    labels.forEach(this::accept);
  }
}
