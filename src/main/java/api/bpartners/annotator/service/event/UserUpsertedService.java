package api.bpartners.annotator.service.event;

import api.bpartners.annotator.endpoint.event.model.UserUpserted;
import api.bpartners.annotator.endpoint.rest.security.cognito.CognitoComponent;
import api.bpartners.annotator.repository.model.User;
import api.bpartners.annotator.service.UserService;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserUpsertedService implements Consumer<UserUpserted> {
  private final CognitoComponent cognitoComponent;
  private final UserService userService;

  @Override
  public void accept(UserUpserted userUpserted) {
    User user = userUpserted.getUser();
    cognitoComponent.createUser(user.getEmail(), user.getTeam().getName());
    userService.save(user);
  }
}
