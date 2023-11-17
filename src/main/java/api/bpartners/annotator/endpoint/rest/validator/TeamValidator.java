package api.bpartners.annotator.endpoint.rest.validator;

import api.bpartners.annotator.endpoint.rest.model.Team;
import api.bpartners.annotator.model.exception.BadRequestException;
import java.util.function.Consumer;
import org.springframework.stereotype.Component;

@Component
public class TeamValidator implements Consumer<Team> {
  @Override
  public void accept(Team team) {
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
