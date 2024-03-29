package api.bpartners.annotator.integration;

import static api.bpartners.annotator.integration.conf.utils.TestMocks.team1;
import static api.bpartners.annotator.integration.conf.utils.TestUtils.setUpCognito;
import static org.junit.Assert.assertEquals;

import api.bpartners.annotator.conf.FacadeIT;
import api.bpartners.annotator.endpoint.rest.api.SecurityApi;
import api.bpartners.annotator.endpoint.rest.client.ApiClient;
import api.bpartners.annotator.endpoint.rest.client.ApiException;
import api.bpartners.annotator.endpoint.rest.model.User;
import api.bpartners.annotator.endpoint.rest.model.UserRole;
import api.bpartners.annotator.endpoint.rest.security.cognito.CognitoComponent;
import api.bpartners.annotator.integration.conf.utils.TestMocks;
import api.bpartners.annotator.integration.conf.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@AutoConfigureMockMvc
public class UserIT extends FacadeIT {
  @MockBean private CognitoComponent cognitoComponent;

  @LocalServerPort private int port;

  static User restJoeDoeUser() {
    return new User()
        .id("joe_doe_id")
        .email("joe@email.com")
        .team(team1())
        .role(UserRole.ANNOTATOR);
  }

  private ApiClient anApiClient() {
    return TestUtils.anApiClient(TestMocks.JOE_DOE_TOKEN, null, port);
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
}
