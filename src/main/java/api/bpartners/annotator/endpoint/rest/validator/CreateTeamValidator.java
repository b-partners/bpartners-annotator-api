package api.bpartners.annotator.endpoint.rest.validator;

import api.bpartners.annotator.endpoint.rest.model.CreateTeam;
import api.bpartners.annotator.model.exception.BadRequestException;
import java.util.function.Consumer;
import org.springframework.stereotype.Component;

@Component
public class CreateTeamValidator implements Consumer<CreateTeam> {
  @Override
  public void accept(CreateTeam team) {
    StringBuilder exceptionMessageBuilder = new StringBuilder();
    if (team.getName() == null) {
      exceptionMessageBuilder.append("Team name is mandatory.");
    }
    String exceptionMessage = exceptionMessageBuilder.toString();
    if (!exceptionMessage.isBlank()) {
      throw new BadRequestException(exceptionMessage);
    }
  }
}
