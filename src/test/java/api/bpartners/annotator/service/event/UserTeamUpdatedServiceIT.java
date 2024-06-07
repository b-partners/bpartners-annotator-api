package api.bpartners.annotator.service.event;

import static java.util.UUID.randomUUID;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import api.bpartners.annotator.conf.FacadeIT;
import api.bpartners.annotator.endpoint.event.model.UserTeamUpdated;
import api.bpartners.annotator.endpoint.rest.security.cognito.CognitoComponent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class UserTeamUpdatedServiceIT extends FacadeIT {
  @Autowired private UserTeamUpdatedService subject;
  @MockBean private CognitoComponent cognitoComponentMock;

  @Test
  void shouldUpdateUserTeam() {
    var groupName = randomUUID().toString();
    var username = randomUUID().toString();
    setupCognitoComponentMock(groupName, username);

    subject.accept(UserTeamUpdated.builder().group(groupName).username(username).build());

    verify(cognitoComponentMock, times(1)).addUserToGroup(groupName, username);
  }

  private void setupCognitoComponentMock(String groupName, String username) {
    doNothing().when(cognitoComponentMock).addUserToGroup(groupName, username);
  }
}
