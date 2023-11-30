package api.bpartners.annotator.integration;

import static api.bpartners.annotator.endpoint.rest.model.JobStatus.PENDING;
import static api.bpartners.annotator.endpoint.rest.model.JobStatus.READY;
import static api.bpartners.annotator.endpoint.rest.model.JobStatus.STARTED;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.job1;
import static api.bpartners.annotator.integration.conf.utils.TestUtils.assertThrowsBadRequestException;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import api.bpartners.annotator.conf.FacadeIT;
import api.bpartners.annotator.endpoint.event.EventProducer;
import api.bpartners.annotator.endpoint.rest.api.JobsApi;
import api.bpartners.annotator.endpoint.rest.client.ApiClient;
import api.bpartners.annotator.endpoint.rest.client.ApiException;
import api.bpartners.annotator.endpoint.rest.model.CrupdateJob;
import api.bpartners.annotator.endpoint.rest.model.Job;
import api.bpartners.annotator.endpoint.rest.model.Label;
import api.bpartners.annotator.endpoint.rest.model.TaskStatistics;
import api.bpartners.annotator.integration.conf.utils.TestMocks;
import api.bpartners.annotator.integration.conf.utils.TestUtils;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class JobIT extends FacadeIT {
  @LocalServerPort private int port;
  @MockBean public EventProducer eventProducer;

  private ApiClient anApiClient() {
    return TestUtils.anApiClient(null, TestMocks.ADMIN_API_KEY, port);
  }

  static CrupdateJob crupdateJob1() {
    return new CrupdateJob()
        .id(randomUUID().toString())
        .status(PENDING)
        .folderPath(null)
        .ownerEmail("admin@gmail.com")
        .bucketName("bucket-name")
        .teamId("team_1_id")
        .labels(List.of(new Label().id("label_5_id").name("POOL").color("#00ff00")));
  }

  static Job createJobFrom(CrupdateJob crupdateJob, TaskStatistics taskStatistics) {
    return new Job()
        .id(crupdateJob.getId())
        .taskStatistics(taskStatistics)
        .bucketName(crupdateJob.getBucketName())
        .teamId(crupdateJob.getTeamId())
        .status(crupdateJob.getStatus())
        .folderPath(crupdateJob.getFolderPath() == null ? "" : crupdateJob.getFolderPath())
        .ownerEmail(crupdateJob.getOwnerEmail())
        .name(crupdateJob.getName())
        .labels(crupdateJob.getLabels());
  }

  @Test
  void admin_get_jobs_ok() throws ApiException {
    ApiClient adminClient = anApiClient();
    JobsApi api = new JobsApi(adminClient);

    List<Job> actualJobs = api.getJobs();

    assertEquals(1, actualJobs.size());
    assertTrue(actualJobs.contains(job1()));
  }

  @Test
  void admin_get_job_by_id() throws ApiException {
    ApiClient adminClient = anApiClient();
    JobsApi api = new JobsApi(adminClient);

    Job actual = api.getJob("job_1_id");

    assertEquals(job1(), actual);
  }

  @Test
  void admin_crupdate_job_ok() throws ApiException {
    ApiClient adminClient = anApiClient();
    JobsApi api = new JobsApi(adminClient);

    // Create//
    CrupdateJob toCreate = crupdateJob1();
    Job actual = api.saveJob(toCreate.getId(), toCreate);
    Job expected =
        createJobFrom(
            toCreate,
            new TaskStatistics().totalTasks(0L).remainingTasks(0L).completedTasksByUserId(0L));

    assertEquals(expected, actual);
    // Create//

    // Update//
    CrupdateJob toUpdate =
        toCreate.name("new name").status(READY).ownerEmail("newEmail@email.com");

    Job updated = api.saveJob(toUpdate.getId(), toUpdate);

    Job expectedAfterUpdate =
        createJobFrom(
            toUpdate,
            new TaskStatistics().totalTasks(0L).remainingTasks(0L).completedTasksByUserId(0L));
    assertEquals(toCreate.getId(), actual.getId());
    assertEquals(expectedAfterUpdate, updated);
    // Update//
  }

  @Test
  void admin_create_job_ko() {
    ApiClient adminClient = anApiClient();
    JobsApi api = new JobsApi(adminClient);
    CrupdateJob invalidCrupdateJob =
        crupdateJob1().id(null).folderPath("/a").ownerEmail(null).labels(Collections.emptyList());

    assertThrowsBadRequestException(
        () -> api.saveJob(randomUUID().toString(), invalidCrupdateJob),
        "ID is mandatory."
            + "folder path: /a does not follow regex ^(?!/).+/$."
            + "Owner Email is mandatory."
            + "Labels are mandatory.");
  }
}
