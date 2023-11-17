package api.bpartners.annotator.service.event;

import api.bpartners.annotator.endpoint.event.gen.TeamsUpserted;
import api.bpartners.annotator.endpoint.rest.security.cognito.CognitoComponent;
import java.util.function.Consumer;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class TeamsUpsertedService implements Consumer<TeamsUpserted> {
  private final CognitoComponent cognitoComponent;

  @Transactional
  @Override
  public void accept(TeamsUpserted teamsUpserted) {
    teamsUpserted.getTeamsToCreate().stream()
        .peek(team -> cognitoComponent.createGroup(team.getName()));
  }
}
