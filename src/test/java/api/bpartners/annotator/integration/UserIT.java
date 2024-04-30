package api.bpartners.annotator.integration;

import static api.bpartners.annotator.integration.conf.utils.TestMocks.TEAM_1_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.TEAM_2_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.team1;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.team2;
import static api.bpartners.annotator.integration.conf.utils.TestUtils.setUpCognito;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import api.bpartners.annotator.conf.FacadeIT;
import api.bpartners.annotator.endpoint.event.EventProducer;
import api.bpartners.annotator.endpoint.rest.api.SecurityApi;
import api.bpartners.annotator.endpoint.rest.api.UsersApi;
import api.bpartners.annotator.endpoint.rest.client.ApiClient;
import api.bpartners.annotator.endpoint.rest.client.ApiException;
import api.bpartners.annotator.endpoint.rest.model.CreateUser;
import api.bpartners.annotator.endpoint.rest.model.Team;
import api.bpartners.annotator.endpoint.rest.model.TeamUser;
import api.bpartners.annotator.endpoint.rest.model.User;
import api.bpartners.annotator.endpoint.rest.model.UserRole;
import api.bpartners.annotator.endpoint.rest.security.cognito.CognitoComponent;
import api.bpartners.annotator.integration.conf.utils.TestMocks;
import api.bpartners.annotator.integration.conf.utils.TestUtils;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@AutoConfigureMockMvc
public class UserIT extends FacadeIT {
  public static final String TEST_USER_ID = "test_user_id";
  @MockBean private CognitoComponent cognitoComponent;

  @LocalServerPort private int port;
  @MockBean EventProducer eventProducerMock;

  static User restJoeDoeUser() {
    return new User()
        .id("joe_doe_id")
        .email("joe@email.com")
        .team(team1())
        .role(UserRole.ANNOTATOR);
  }

  static User testUser() {
    return new User()
        .id(TEST_USER_ID)
        .email("test@email.com")
        .team(team1())
        .role(UserRole.ANNOTATOR);
  }

  private ApiClient anApiClient() {
    return TestUtils.anApiClient(TestMocks.JOE_DOE_TOKEN, null, port);
  }

  private ApiClient adminApiClient() {
    return TestUtils.anApiClient(null, TestMocks.ADMIN_API_KEY, port);
  }

  @BeforeEach
  public void setUp() {
    setUpCognito(cognitoComponent);
  }

  @Test
  void user_read_own_information_ok() throws ApiException {
    ApiClient joeDoeClient = anApiClient();
    SecurityApi api = new SecurityApi(joeDoeClient);

    User actualUser = api.whoami().getUser();

    assertEquals(restJoeDoeUser(), actualUser);
  }

  @Test
  void create_user_ok() throws ApiException {
    ApiClient admin = adminApiClient();
    UsersApi api = new UsersApi(admin);
    List<CreateUser> payload =
        List.of(
            new CreateUser().email("dummy@email.com").role(UserRole.ANNOTATOR).teamId(TEAM_1_ID));
    var expected = payload.stream().map(UserIT::createUserWithoutIdAndTeamNameFrom).toList();

    var created = api.createUsers(payload);

    verify(eventProducerMock, times(payload.size())).accept(anyList());
    assertEquals(expected, created.stream().map(UserIT::ignoreIdAndTeamNameOf).toList());
  }

  @Test
  void get_all_users_ok() throws ApiException {
    ApiClient admin = adminApiClient();
    UsersApi api = new UsersApi(admin);

    List<User> actualUsers = api.getUsers(1, 100);

    assertTrue(actualUsers.contains(restJoeDoeUser()));
  }

  @Test
  void update_user_team_ok() throws ApiException {
    ApiClient admin = adminApiClient();
    UsersApi api = new UsersApi(admin);

    var expected = testUser().team(team2());
    var actual =
        api.updateUserTeam(TEST_USER_ID, new TeamUser().userId(TEST_USER_ID).teamId(TEAM_2_ID));

    assertEquals(expected, actual);
  }

  private static User createUserWithoutIdAndTeamNameFrom(CreateUser createUser) {
    return new User()
        .id(null)
        .email(createUser.getEmail())
        .role(createUser.getRole())
        .team(new Team().id(createUser.getTeamId()).name(null));
  }

  private static User ignoreIdAndTeamNameOf(User user) {
    var team = user.getTeam().name(null);
    return user.id(null).team(team);
  }
}
