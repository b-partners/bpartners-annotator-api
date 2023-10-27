package api.bpartners.annotator.integration;

import api.bpartners.annotator.endpoint.rest.api.JobsApi;
import api.bpartners.annotator.endpoint.rest.client.ApiClient;
import api.bpartners.annotator.endpoint.rest.client.ApiException;
import api.bpartners.annotator.endpoint.rest.model.CrupdateJob;
import api.bpartners.annotator.endpoint.rest.model.Job;
import api.bpartners.annotator.integration.conf.AbstractContextInitializer;
import api.bpartners.annotator.integration.conf.utils.TestUtils;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;

import static api.bpartners.annotator.endpoint.rest.model.JobStatus.PENDING;
import static api.bpartners.annotator.integration.conf.utils.MockData.JOB_1_ID;
import static api.bpartners.annotator.integration.conf.utils.MockData.TEAM_1_ID;
import static api.bpartners.annotator.integration.conf.utils.MockData.job1;
import static api.bpartners.annotator.integration.conf.utils.MockData.label1;
import static api.bpartners.annotator.integration.conf.utils.TestUtils.anAvailableRandomPort;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Testcontainers
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ContextConfiguration(initializers = JobIT.ContextInitializer.class)
class JobIT {

  private static ApiClient anApiClient(String token) {
    return TestUtils.anApiClient(token, ContextInitializer.SERVER_PORT);
  }

  static Job job(CrupdateJob crupdateJob, int remainingTasks) {
    return new Job().id(crupdateJob.getId())
        .ownerEmail(crupdateJob.getOwnerEmail())
        .folderPath(crupdateJob.getFolderPath())
        .bucketName(crupdateJob.getBucketName())
        .labels(crupdateJob.getLabels())
        .teamId(crupdateJob.getTeamId())
        .status(crupdateJob.getStatus())
        .remainingTasks(remainingTasks);
  }

  static CrupdateJob crupdateJob() {
    return new CrupdateJob()
        .id(randomUUID().toString())
        .teamId(TEAM_1_ID)
        .labels(List.of(label1()))
        .status(PENDING)
        .bucketName("mock")
        .folderPath("mock")
        .ownerEmail("dummy@hotmail.com");
  }

  @Test
  void get_all_jobs_ok() throws ApiException {
    ApiClient client = anApiClient("client_token");
    var api = new JobsApi(client);

    List<Job> actual = api.getJobs();

    assertEquals(List.of(job1()), actual);
  }

  @Test
  void get_job_ok() throws ApiException {
    ApiClient client = anApiClient("client_token");
    var api = new JobsApi(client);

    Job actual = api.getJob(JOB_1_ID);

    assertEquals(job1(), actual);
  }

  @Test
  void create_job_ok() throws ApiException {
    ApiClient client = anApiClient("client_token");
    var api = new JobsApi(client);
    var id = randomUUID().toString();
    CrupdateJob crupdateJob = crupdateJob();

    Job actual = api.saveJob(id, crupdateJob);

    assertEquals(job(crupdateJob, 0), actual);
  }

  static class ContextInitializer extends AbstractContextInitializer {
    private static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }

}
