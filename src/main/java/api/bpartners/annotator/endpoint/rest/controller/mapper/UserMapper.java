package api.bpartners.annotator.endpoint.rest.controller.mapper;

import static java.util.UUID.randomUUID;

import api.bpartners.annotator.endpoint.rest.model.CreateUser;
import api.bpartners.annotator.endpoint.rest.model.TeamUser;
import api.bpartners.annotator.endpoint.rest.model.User;
import api.bpartners.annotator.endpoint.rest.security.model.Role;
import api.bpartners.annotator.endpoint.rest.validator.TeamUserValidator;
import api.bpartners.annotator.endpoint.rest.validator.UserValidator;
import api.bpartners.annotator.repository.model.Team;
import api.bpartners.annotator.service.TeamService;
import api.bpartners.annotator.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserMapper {
  private final UserRoleMapper roleMapper;
  private final UserValidator validator;
  private final TeamUserValidator teamUserValidator;
  private final TeamMapper teamMapper;
  private final TeamService teamService;
  private final UserService userService;

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
        .id(randomUUID().toString())
        .email(rest.getEmail())
        .roles(new Role[] {roleMapper.toDomain(rest.getRole())})
        .team(teamService.getById(rest.getTeamId()))
        .build();
  }

  public api.bpartners.annotator.repository.model.User toDomain(TeamUser rest) {
    teamUserValidator.accept(rest);
    api.bpartners.annotator.repository.model.User actualUser = userService.getById(rest.getUserId());
    Team actualTeam = teamService.getById(rest.getTeamId());
    actualUser.setTeam(actualTeam);
    return actualUser;
  }
}
