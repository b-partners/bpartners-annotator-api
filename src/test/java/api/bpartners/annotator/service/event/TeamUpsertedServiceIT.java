package api.bpartners.annotator.service.event;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import api.bpartners.annotator.conf.FacadeIT;
import api.bpartners.annotator.endpoint.event.model.TeamUpserted;
import api.bpartners.annotator.endpoint.rest.security.cognito.CognitoComponent;
import api.bpartners.annotator.repository.model.Team;
import api.bpartners.annotator.service.TeamService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class TeamUpsertedServiceIT extends FacadeIT {
  @Autowired private TeamUpsertedService subject;
  @MockBean private CognitoComponent cognitoComponentMock;
  @Autowired private TeamService teamService;

  @Test
  void accept_ok() {
    String id = randomUUID().toString();
    String name = randomUUID().toString();
    setupCognitoComponentMock(name);
    Team expectedTeam = Team.builder().id(id).name(name).build();

    subject.accept(new TeamUpserted(expectedTeam));
    var actual = teamService.getById(id);

    verify(cognitoComponentMock, times(1)).createGroup(name);
    assertEquals(expectedTeam, actual);
  }

  private void setupCognitoComponentMock(String name) {
    doNothing().when(cognitoComponentMock).createGroup(name);
  }
}
