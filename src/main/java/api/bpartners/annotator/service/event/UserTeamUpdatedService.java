package api.bpartners.annotator.service.event;

import api.bpartners.annotator.endpoint.event.gen.UserTeamUpdated;
import api.bpartners.annotator.endpoint.rest.security.authentication.bearer.cognito.CognitoComponent;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserTeamUpdatedService implements Consumer<UserTeamUpdated> {
  private final CognitoComponent cognitoComponent;

  @Override
  public void accept(UserTeamUpdated userTeamUpdated) {
    cognitoComponent.addUserToGroup(userTeamUpdated.getGroup(), userTeamUpdated.getUsername());
  }
}
