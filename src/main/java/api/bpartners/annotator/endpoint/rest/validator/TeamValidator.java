package api.bpartners.annotator.endpoint.rest.validator;

import api.bpartners.annotator.endpoint.event.EventProducer;
import api.bpartners.annotator.endpoint.event.gen.TeamsUpserted;
import api.bpartners.annotator.endpoint.rest.model.Team;
import api.bpartners.annotator.model.exception.BadRequestException;
import java.util.List;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TeamValidator implements Consumer<Team> {
  private final EventProducer eventProducer;

  public void accept(List<Team> teams) {
    teams.forEach(this);
    eventProducer.accept(List.of(toEventType(teams)));
  }

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

  public TeamsUpserted toEventType(List<Team> teams) {
    return new TeamsUpserted(teams);
  }
}
