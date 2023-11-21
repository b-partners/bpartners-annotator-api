package api.bpartners.annotator.endpoint.rest.controller.mapper;

import api.bpartners.annotator.endpoint.rest.model.CreateUser;
import api.bpartners.annotator.endpoint.rest.model.User;
import api.bpartners.annotator.endpoint.rest.security.model.Role;
import api.bpartners.annotator.endpoint.rest.validator.UserValidator;
import api.bpartners.annotator.service.TeamService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserMapper {
  private final UserRoleMapper roleMapper;
  private final UserValidator validator;
  private final TeamMapper teamMapper;
  private final TeamService teamService;

  public User toRest(api.bpartners.annotator.repository.model.User domain) {
    return new User()
        .id(domain.getId())
        .email(domain.getEmail())
        .role(roleMapper.toRest(domain.getRoles()[0]))
        .team(teamMapper.toRest(domain.getTeam()));
  }

  public api.bpartners.annotator.repository.model.User toDomain(CreateUser rest) {
    validator.accept(rest);
    return api.bpartners.annotator.repository.model.User.builder()
        .email(rest.getEmail())
        .roles(new Role[] {roleMapper.toDomain(rest.getRole())})
        .team(teamService.getById(rest.getTeamId()))
        .build();
  }
}
