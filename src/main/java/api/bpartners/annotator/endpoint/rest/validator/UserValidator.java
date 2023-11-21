package api.bpartners.annotator.endpoint.rest.validator;

import api.bpartners.annotator.endpoint.rest.model.CreateUser;
import api.bpartners.annotator.endpoint.rest.model.UserRole;
import api.bpartners.annotator.model.exception.BadRequestException;
import java.util.function.Consumer;
import org.springframework.stereotype.Component;

@Component
public class UserValidator implements Consumer<CreateUser> {
  @Override
  public void accept(CreateUser user) {
    StringBuilder exceptionMessageBuilder = new StringBuilder();
    if (user.getEmail() == null) {
      exceptionMessageBuilder.append("email is mandatory");
    }
    if (user.getEmail().isBlank()) {
      exceptionMessageBuilder.append("email must not be blank. ");
    }
    if (user.getRole() == null) {
      exceptionMessageBuilder.append("role is mandatory. ");
    }
    if (user.getRole() != UserRole.ANNOTATOR) {
      exceptionMessageBuilder.append("only annotator role is supported. ");
    }
    String exceptionMessage = exceptionMessageBuilder.toString();
    if (!exceptionMessage.isBlank()) {
      throw new BadRequestException(exceptionMessage);
    }
  }
}
