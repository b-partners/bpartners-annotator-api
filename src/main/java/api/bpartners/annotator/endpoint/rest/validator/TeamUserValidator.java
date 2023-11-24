package api.bpartners.annotator.endpoint.rest.validator;

import api.bpartners.annotator.endpoint.rest.model.TeamUser;
import api.bpartners.annotator.model.exception.BadRequestException;
import java.util.function.Consumer;
import org.springframework.stereotype.Component;

@Component
public class TeamUserValidator implements Consumer<TeamUser> {
  @Override
  public void accept(TeamUser teamUser) {
    StringBuilder exceptionMessageBuilder = new StringBuilder();
    if (teamUser.getTeamId() == null) {
      exceptionMessageBuilder.append("Team id is mandatory.");
    }
    if (teamUser.getUserId() == null) {
      exceptionMessageBuilder.append("User id is mandatory.");
    }
    String exceptionMessage = exceptionMessageBuilder.toString();
    if (!exceptionMessage.isBlank()) {
      throw new BadRequestException(exceptionMessage);
    }
  }
}
