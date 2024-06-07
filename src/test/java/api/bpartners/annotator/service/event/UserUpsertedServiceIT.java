package api.bpartners.annotator.service.event;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import api.bpartners.annotator.conf.FacadeIT;
import api.bpartners.annotator.endpoint.event.model.UserUpserted;
import api.bpartners.annotator.endpoint.rest.security.cognito.CognitoComponent;
import api.bpartners.annotator.repository.model.Team;
import api.bpartners.annotator.repository.model.User;
import api.bpartners.annotator.service.TeamService;
import api.bpartners.annotator.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class UserUpsertedServiceIT extends FacadeIT {
  @Autowired private UserUpsertedService subject;
  @MockBean private CognitoComponent cognitoComponentMock;
  @Autowired private UserService userService;
  @Autowired private TeamService teamService;

  Team team() {
    String teamId = randomUUID().toString();
    String teamName = randomUUID().toString();
    return teamService.save(Team.builder().id(teamId).name(teamName).build());
  }

  @Test
  void accept_ok() {
    String id = randomUUID().toString();
    String groupName = randomUUID().toString();
    setupCognitoComponentMock(groupName);
    String email = randomUUID().toString();
    Team team = team();
    User expectedUser = User.builder().id(id).email(email).team(team).build();

    subject.accept(UserUpserted.builder().user(expectedUser).build());
    var actual = userService.getById(id);

    verify(cognitoComponentMock).createUser(email, team.getName());
    assertEquals(expectedUser, actual);
  }

  private void setupCognitoComponentMock(String groupName) {
    doNothing().when(cognitoComponentMock).createGroup(groupName);
  }
}
