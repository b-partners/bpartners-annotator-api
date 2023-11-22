package api.bpartners.annotator.endpoint.rest.controller.mapper;

import api.bpartners.annotator.endpoint.rest.model.CreateTeam;
import api.bpartners.annotator.endpoint.rest.model.Team;
import org.springframework.stereotype.Component;

@Component
public class TeamMapper {
  public Team toRest(api.bpartners.annotator.repository.model.Team domain) {
    return new Team().id(domain.getId()).name(domain.getName());
  }

  public api.bpartners.annotator.repository.model.Team toDomain(CreateTeam rest) {
    return api.bpartners.annotator.repository.model.Team.builder().name(rest.getName()).build();
  }
}
