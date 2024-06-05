package api.bpartners.annotator.service.event;

import api.bpartners.annotator.endpoint.event.model.TeamUpserted;
import api.bpartners.annotator.endpoint.rest.security.cognito.CognitoComponent;
import api.bpartners.annotator.service.TeamService;
import jakarta.transaction.Transactional;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class TeamUpsertedService implements Consumer<TeamUpserted> {
  private final CognitoComponent cognitoComponent;
  private final TeamService teamService;

  @Transactional
  @Override
  public void accept(TeamUpserted teamUpserted) {
    cognitoComponent.createGroup(teamUpserted.getTeam().getName());
    teamService.save(teamUpserted.getTeam());
  }
}
