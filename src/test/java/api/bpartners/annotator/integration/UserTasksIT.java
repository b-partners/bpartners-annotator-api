package api.bpartners.annotator.integration;

import static api.bpartners.annotator.endpoint.rest.model.TaskStatus.UNDER_COMPLETION;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.JOB_1_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.JOE_DOE_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.TEAM_1_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.task1;
import static api.bpartners.annotator.integration.conf.utils.TestUtils.assertThrowsForbiddenException;
import static api.bpartners.annotator.integration.conf.utils.TestUtils.setUpCognito;
import static api.bpartners.annotator.integration.conf.utils.TestUtils.setUpS3Service;
import static org.junit.jupiter.api.Assertions.assertEquals;

import api.bpartners.annotator.conf.FacadeIT;
import api.bpartners.annotator.endpoint.rest.api.UserTasksApi;
import api.bpartners.annotator.endpoint.rest.client.ApiClient;
import api.bpartners.annotator.endpoint.rest.client.ApiException;
import api.bpartners.annotator.endpoint.rest.model.Task;
import api.bpartners.annotator.endpoint.rest.security.cognito.CognitoComponent;
import api.bpartners.annotator.integration.conf.utils.TestMocks;
import api.bpartners.annotator.integration.conf.utils.TestUtils;
import api.bpartners.annotator.service.aws.JobOrTaskS3Service;
import java.net.MalformedURLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;

class UserTasksIT extends FacadeIT {
  @LocalServerPort private int port;
  @MockBean private CognitoComponent cognitoComponent;
  @MockBean public JobOrTaskS3Service fileService;

  private ApiClient adminApiClient() {
    return TestUtils.anApiClient(null, TestMocks.ADMIN_API_KEY, port);
  }

  private ApiClient joeDoeClient() {
    return TestUtils.anApiClient(TestMocks.JOE_DOE_TOKEN, null, port);
  }

  @BeforeEach
  public void setUp() throws MalformedURLException {
    setUpCognito(cognitoComponent);
    setUpS3Service(fileService);
  }

  @Test
  void admin_get_task_ko() {
    ApiClient adminClient = adminApiClient();
    UserTasksApi api = new UserTasksApi(adminClient);

    assertThrowsForbiddenException(
        () -> api.getUserTaskByJob(TEAM_1_ID, JOB_1_ID), "Access Denied");
  }

  @Test
  void user_get_task_ok() throws ApiException {
    ApiClient joeDoeClient = joeDoeClient();
    UserTasksApi api = new UserTasksApi(joeDoeClient);

    Task actual = api.getUserTaskByJob(TEAM_1_ID, JOB_1_ID);

    assertEquals(task1().userId(JOE_DOE_ID).status(UNDER_COMPLETION), actual);
  }
}
