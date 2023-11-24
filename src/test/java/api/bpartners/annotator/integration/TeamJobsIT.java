package api.bpartners.annotator.integration;

import static api.bpartners.annotator.endpoint.rest.model.JobStatus.STARTED;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.JOB_1_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.TEAM_1_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.job1;
import static api.bpartners.annotator.integration.conf.utils.TestUtils.setUpCognito;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import api.bpartners.annotator.conf.FacadeIT;
import api.bpartners.annotator.endpoint.rest.api.TeamJobsApi;
import api.bpartners.annotator.endpoint.rest.client.ApiClient;
import api.bpartners.annotator.endpoint.rest.client.ApiException;
import api.bpartners.annotator.endpoint.rest.model.Job;
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
public class TeamJobsIT extends FacadeIT {
  @LocalServerPort private int port;
  @MockBean private CognitoComponent cognitoComponent;

  private ApiClient anApiClient() {
    return TestUtils.anApiClient(TestMocks.JOE_DOE_TOKEN, null, port);
  }

  @BeforeEach
  public void setUp() {
    setUpCognito(cognitoComponent);
  }

  @Test
  void get_team_jobs_ok() throws ApiException {
    ApiClient joeDoeClient = anApiClient();
    TeamJobsApi api = new TeamJobsApi(joeDoeClient);

    List<Job> actual = api.getTeamJobs(TEAM_1_ID);

    assertTrue(actual.contains(job1()));
    assertTrue(actual.stream().allMatch(job -> job.getStatus().equals(STARTED)));
  }

  @Test
  void get_team_jobs_by_id_ok() throws ApiException {
    ApiClient joeDoeClient = anApiClient();
    TeamJobsApi api = new TeamJobsApi(joeDoeClient);

    Job actual = api.getTeamJobById(TEAM_1_ID, JOB_1_ID);

    assertEquals(job1(), actual);
  }
}