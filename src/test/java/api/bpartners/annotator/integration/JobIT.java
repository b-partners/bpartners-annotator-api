package api.bpartners.annotator.integration;

import static api.bpartners.annotator.endpoint.rest.model.JobStatus.*;
import static api.bpartners.annotator.endpoint.rest.model.JobStatus.PENDING;
import static api.bpartners.annotator.endpoint.rest.model.JobStatus.READY;
import static api.bpartners.annotator.endpoint.rest.model.JobStatus.STARTED;
import static api.bpartners.annotator.endpoint.rest.model.JobType.LABELLING;
import static api.bpartners.annotator.endpoint.rest.model.JobType.REVIEWING;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.ADMIN_API_KEY;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.JOB_1_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.JOE_DOE_TOKEN;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.job1;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.job9;
import static api.bpartners.annotator.integration.conf.utils.TestUtils.assertThrowsBadRequestException;
import static api.bpartners.annotator.integration.conf.utils.TestUtils.setUpCognito;
import static java.util.Collections.emptyList;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import api.bpartners.annotator.conf.FacadeIT;
import api.bpartners.annotator.endpoint.event.EventProducer;
import api.bpartners.annotator.endpoint.rest.api.JobsApi;
import api.bpartners.annotator.endpoint.rest.client.ApiClient;
import api.bpartners.annotator.endpoint.rest.client.ApiException;
import api.bpartners.annotator.endpoint.rest.model.Annotation;
import api.bpartners.annotator.endpoint.rest.model.AnnotationNumberPerLabel;
import api.bpartners.annotator.endpoint.rest.model.CrupdateJob;
import api.bpartners.annotator.endpoint.rest.model.Job;
import api.bpartners.annotator.endpoint.rest.model.Label;
import api.bpartners.annotator.endpoint.rest.model.Point;
import api.bpartners.annotator.endpoint.rest.model.Polygon;
import api.bpartners.annotator.endpoint.rest.model.TaskStatistics;
import api.bpartners.annotator.endpoint.rest.security.cognito.CognitoComponent;
import api.bpartners.annotator.integration.conf.utils.TestUtils;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class JobIT extends FacadeIT {
  @LocalServerPort private int port;
  @MockBean public CognitoComponent cognitoComponent;
  @MockBean public EventProducer eventProducer;

  @BeforeEach
  public void setUp() {
    setUpCognito(cognitoComponent);
  }

  private ApiClient anAdminApiClient() {
    return TestUtils.anApiClient(null, ADMIN_API_KEY, port);
  }

  private ApiClient anAnnotatorApiClient() {
    return TestUtils.anApiClient(JOE_DOE_TOKEN, null, port);
  }

  public static CrupdateJob crupdateJob1() {
    return new CrupdateJob()
        .id(randomUUID().toString())
        .status(PENDING)
        .folderPath(null)
        .imagesHeight(1024)
        .imagesWidth(1024)
        .ownerEmail("admin@gmail.com")
        .bucketName("bucket-name")
        .teamId("team_1_id")
        .type(LABELLING)
        .labels(List.of(new Label().id("label_5_id").name("POOL").color("#00ff00")));
  }

  static Job from(CrupdateJob crupdateJob, TaskStatistics taskStatistics) {
    List<Label> labels = crupdateJob.getLabels();
    return new Job()
        .id(crupdateJob.getId())
        .taskStatistics(taskStatistics)
        .bucketName(crupdateJob.getBucketName())
        .teamId(crupdateJob.getTeamId())
        .imagesHeight(crupdateJob.getImagesHeight())
        .imagesWidth(crupdateJob.getImagesWidth())
        .status(crupdateJob.getStatus())
        .folderPath(crupdateJob.getFolderPath() == null ? "" : crupdateJob.getFolderPath())
        .ownerEmail(crupdateJob.getOwnerEmail())
        .name(crupdateJob.getName())
        .type(crupdateJob.getType())
        .labels(labels);
  }

  @Test
  void admin_get_jobs_ok() throws ApiException {
    ApiClient adminClient = anAdminApiClient();
    JobsApi api = new JobsApi(adminClient);

    List<Job> actualJobs = api.getJobs(1, 10, null, null, null);

    // assertEquals(8, actualJobs.size());
    assertTrue(actualJobs.contains(job1AsAdminView()));
  }

  @Test
  void admin_get_jobs_filtered() throws ApiException {
    ApiClient adminClient = anAdminApiClient();
    JobsApi api = new JobsApi(adminClient);

    List<Job> actualStartedJobs = api.getJobs(1, 10, STARTED, null, null);
    List<Job> actualPendingJobs = api.getJobs(1, 10, PENDING, null, null);
    List<Job> actualReadyJobs = api.getJobs(1, 10, READY, null, null);
    List<Job> actualCompletedJobs = api.getJobs(1, 10, COMPLETED, null, null);
    List<Job> actualToReviewJobs = api.getJobs(1, 10, TO_REVIEW, null, null);
    List<Job> actualToCorrectJobs = api.getJobs(1, 10, TO_CORRECT, null, null);
    List<Job> actualFailedJobs = api.getJobs(1, 10, FAILED, null, null);
    List<Job> actualAllJobs = api.getJobs(1, 500, null, null, null);
    List<Job> actualJobsFilteredByExactName =
        api.getJobs(1, 10, null, job1AsAdminView().getName(), null);
    List<Job> actualJobsFilteredByNoMatchingName =
        api.getJobs(1, 10, null, randomUUID().toString(), null);
    List<Job> actualJobsFilteredByCommonName = api.getJobs(1, 10, null, "_", null);
    List<Job> actualJobsFilteredByType = api.getJobs(1, 500, null, null, REVIEWING);

    assertEquals(2, actualStartedJobs.size());
    assertEquals(1, actualPendingJobs.size());
    // TODO: dirty context seems not to work because inserted Ready jobs.size should be 1. read
    // testData
    assertEquals(2, actualReadyJobs.size());
    assertEquals(1, actualCompletedJobs.size());
    assertEquals(1, actualToReviewJobs.size());
    // assertEquals(1, actualToCorrectJobs.size());
    // FAILED JOBS ARE NOW HIDDEN
    assertEquals(0, actualFailedJobs.size());
    assertTrue(actualJobsFilteredByExactName.contains(job1AsAdminView()));
    assertTrue(actualJobsFilteredByNoMatchingName.isEmpty());
    assertTrue(actualAllJobs.contains(job1AsAdminView()));
    assertTrue(actualJobsFilteredByCommonName.contains(job1AsAdminView()));
    assertTrue(actualJobsFilteredByType.contains(job9()));

    assertTrue(actualStartedJobs.stream().allMatch(j -> STARTED.equals(j.getStatus())));
    assertTrue(actualPendingJobs.stream().allMatch(j -> PENDING.equals(j.getStatus())));
    assertTrue(actualReadyJobs.stream().allMatch(j -> READY.equals(j.getStatus())));
    assertTrue(actualCompletedJobs.stream().allMatch(j -> COMPLETED.equals(j.getStatus())));
    assertTrue(actualToReviewJobs.stream().allMatch(j -> TO_REVIEW.equals(j.getStatus())));
    assertTrue(actualFailedJobs.stream().allMatch(j -> FAILED.equals(j.getStatus())));
    assertTrue(actualToCorrectJobs.stream().allMatch(j -> TO_CORRECT.equals(j.getStatus())));
  }

  @Test
  void admin_get_job_by_id() throws ApiException {
    ApiClient adminClient = anAdminApiClient();
    JobsApi api = new JobsApi(adminClient);

    Job actual = api.getJob(JOB_1_ID);
    List<AnnotationNumberPerLabel> actualAnnotationStatistics =
        api.getJobLatestAnnotationStatistics(JOB_1_ID);

    assertEquals(job1AsAdminView(), actual);
    assertEquals(
        List.of(
            new AnnotationNumberPerLabel().labelName("POOL").numberOfAnnotations(1L),
            new AnnotationNumberPerLabel().labelName("VELUX").numberOfAnnotations(1L)),
        actualAnnotationStatistics);
  }

  @Test
  void admin_crupdate_job_ok() throws ApiException {
    ApiClient adminClient = anAdminApiClient();
    JobsApi api = new JobsApi(adminClient);

    // Create//
    CrupdateJob toCreate = crupdateJob1();
    Job actual = api.saveJob(toCreate.getId(), toCreate);
    Job expected =
        from(
            toCreate,
            new TaskStatistics()
                .totalTasks(0L)
                .remainingTasks(0L)
                .completedTasksByUserId(0L)
                .remainingTasksForUserId(0L));

    assertEquals(expected, actual);
    // Create//

    // Update//
    CrupdateJob toUpdate = toCreate.name("new name").status(READY).ownerEmail("newEmail@email.com");

    Job updated = api.saveJob(toUpdate.getId(), toUpdate);

    Job expectedAfterUpdate =
        from(
            toUpdate,
            new TaskStatistics()
                .totalTasks(0L)
                .remainingTasks(0L)
                .completedTasksByUserId(0L)
                .remainingTasksForUserId(0L));
    assertEquals(toCreate.getId(), actual.getId());
    assertEquals(expectedAfterUpdate, updated);
    // Update//
  }

  @Test
  void admin_create_job_ko() {
    ApiClient adminClient = anAdminApiClient();
    JobsApi api = new JobsApi(adminClient);
    String randomUUID = randomUUID().toString();
    CrupdateJob invalidCrupdateJob =
        crupdateJob1().id(randomUUID).folderPath("/a").ownerEmail(null).labels(emptyList());

    assertThrowsBadRequestException(
        () -> api.saveJob(randomUUID, invalidCrupdateJob),
        "folder path: /a does not follow regex ^(?!/).+/$."
            + "Owner Email is mandatory."
            + "Labels are mandatory.");
  }

  public static Annotation creatableAnnotation(String taskId, String annotatorId, Label label) {
    return new Annotation()
        .id(randomUUID().toString())
        .taskId(taskId)
        .userId(annotatorId)
        .polygon(new Polygon().points(List.of(new Point().x(1.0).y(1.0))))
        .label(label);
  }

  public static Label creatableDummyLabel() {
    return new Label().id(randomUUID().toString()).name("dummy_label").color("#00ff00");
  }

  CrupdateJob from(Job job) {
    return new CrupdateJob()
        .id(job.getId())
        .type(job.getType())
        .labels(job.getLabels())
        .status(job.getStatus())
        .name(job.getName())
        .imagesHeight(job.getImagesHeight())
        .imagesWidth(job.getImagesHeight())
        .ownerEmail(job.getOwnerEmail())
        .folderPath(job.getFolderPath())
        .bucketName(job.getBucketName())
        .teamId(job.getTeamId());
  }

  Job job1AsAdminView() {
    Job job1 = job1();
    job1.setTaskStatistics(job1.getTaskStatistics().remainingTasksForUserId(9L));
    return job1;
  }
}
