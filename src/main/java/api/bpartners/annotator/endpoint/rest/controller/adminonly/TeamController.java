package api.bpartners.annotator.endpoint.rest.controller.adminonly;

import api.bpartners.annotator.endpoint.rest.controller.mapper.TeamMapper;
import api.bpartners.annotator.endpoint.rest.model.CreateTeam;
import api.bpartners.annotator.endpoint.rest.model.Team;
import api.bpartners.annotator.endpoint.rest.validator.CreateTeamValidator;
import api.bpartners.annotator.model.BoundedPageSize;
import api.bpartners.annotator.model.PageFromOne;
import api.bpartners.annotator.service.TeamService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@AllArgsConstructor
public class TeamController {
  private final TeamService service;
  private final TeamMapper mapper;
  private final CreateTeamValidator validator;

  @GetMapping("/teams")
  public List<Team> getTeams(
      @RequestParam(required = false, defaultValue = "1") PageFromOne page,
      @RequestParam(required = false, defaultValue = "30") BoundedPageSize pageSize) {
    return service.getAll(page, pageSize).stream().map(mapper::toRest).toList();
  }

  @PostMapping("/teams")
  public List<Team> createTeams(@RequestBody List<CreateTeam> teams) {
    teams.forEach(validator);
    return service.fireEvents(teams.stream().map(mapper::toDomain).toList()).stream()
        .map(mapper::toRest)
        .toList();
  }
}
