package api.bpartners.annotator.integration;

import static api.bpartners.annotator.integration.conf.utils.TestMocks.team1;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.team2;
import static api.bpartners.annotator.integration.conf.utils.TestUtils.assertThrowsBadRequestException;
import static org.junit.jupiter.api.Assertions.assertTrue;

import api.bpartners.annotator.conf.FacadeIT;
import api.bpartners.annotator.endpoint.event.EventProducer;
import api.bpartners.annotator.endpoint.rest.api.TeamsApi;
import api.bpartners.annotator.endpoint.rest.client.ApiClient;
import api.bpartners.annotator.endpoint.rest.client.ApiException;
import api.bpartners.annotator.endpoint.rest.model.CreateTeam;
import api.bpartners.annotator.endpoint.rest.model.Team;
import api.bpartners.annotator.integration.conf.utils.TestMocks;
import api.bpartners.annotator.integration.conf.utils.TestUtils;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;

class TeamIT extends FacadeIT {
  @LocalServerPort private int port;
  @MockBean public EventProducer eventProducer;

  private ApiClient anApiClient() {
    return TestUtils.anApiClient(null, TestMocks.ADMIN_API_KEY, port);
  }

  private static CreateTeam createTeam() {
    return new CreateTeam().name("mock_name");
  }

  private static Team createIdLessTeamFrom(CreateTeam createTeam) {
    return new Team().id(null).name(createTeam.getName());
  }

  @Test
  void admin_get_teams_ok() throws ApiException {
    ApiClient adminClient = anApiClient();
    TeamsApi api = new TeamsApi(adminClient);

    List<Team> actualTeams = api.getTeams(1, 10);

    assertTrue(actualTeams.contains(team1()));
    assertTrue(actualTeams.contains(team2()));
  }

  @Test
  void admin_create_team_ok() throws ApiException {
    ApiClient adminClient = anApiClient();
    TeamsApi api = new TeamsApi(adminClient);
    CreateTeam toCreate = createTeam();

    List<Team> actualCreatedTeams = api.createTeams(List.of(toCreate));

    Team expected = createIdLessTeamFrom(toCreate);
    assertTrue(actualCreatedTeams.stream().map(this::ignoreId).toList().contains(expected));
  }

  Team ignoreId(Team team) {
    return team.id(null);
  }

  @Test
  void admin_create_team_ko() {
    ApiClient adminClient = anApiClient();
    TeamsApi api = new TeamsApi(adminClient);
    CreateTeam toCreate = createTeam().name(null);

    assertThrowsBadRequestException(
        () -> api.createTeams(List.of(toCreate)), "Team name is mandatory.");
  }
}
