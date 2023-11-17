package api.bpartners.annotator.endpoint.rest.controller.mapper;

import api.bpartners.annotator.endpoint.rest.model.UserRole;
import api.bpartners.annotator.endpoint.rest.security.model.Role;
import org.springframework.stereotype.Component;

@Component
public class UserRoleMapper {
  public UserRole toRest(Role role) {
    return switch (role) {
      case ANNOTATOR -> UserRole.ANNOTATOR;
      case ADMIN -> UserRole.ADMIN;
    };
  }

  public Role toDomain(UserRole rest) {
    return switch (rest) {
      case ANNOTATOR -> Role.ANNOTATOR;
      case ADMIN -> Role.ADMIN;
    };
  }
}
