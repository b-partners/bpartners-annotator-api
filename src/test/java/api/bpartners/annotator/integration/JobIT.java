package api.bpartners.annotator.integration;

import static api.bpartners.annotator.endpoint.rest.model.ExportFormat.COCO;
import static api.bpartners.annotator.endpoint.rest.model.ExportFormat.VGG;
import static api.bpartners.annotator.endpoint.rest.model.JobStatus.COMPLETED;
import static api.bpartners.annotator.endpoint.rest.model.JobStatus.FAILED;
import static api.bpartners.annotator.endpoint.rest.model.JobStatus.PENDING;
import static api.bpartners.annotator.endpoint.rest.model.JobStatus.READY;
import static api.bpartners.annotator.endpoint.rest.model.JobStatus.STARTED;
import static api.bpartners.annotator.endpoint.rest.model.JobStatus.TO_CORRECT;
import static api.bpartners.annotator.endpoint.rest.model.JobStatus.TO_REVIEW;
import static api.bpartners.annotator.endpoint.rest.model.JobType.LABELLING;
import static api.bpartners.annotator.endpoint.rest.model.JobType.REVIEWING;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.ADMIN_API_KEY;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.GEOJOBS_TEAM_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.GEOJOBS_USER_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.JOB_1_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.JOE_DOE_TOKEN;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.MOCK_PRESIGNED_URL;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.job1;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.job9;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.polygon;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.team1;
import static api.bpartners.annotator.integration.conf.utils.TestUtils.assertThrowsBadRequestException;
import static api.bpartners.annotator.integration.conf.utils.TestUtils.setUpCognito;
import static api.bpartners.annotator.integration.conf.utils.TestUtils.setUpS3Service;
import static java.util.Collections.emptyList;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import api.bpartners.annotator.conf.FacadeIT;
import api.bpartners.annotator.endpoint.event.EventProducer;
import api.bpartners.annotator.endpoint.rest.api.JobsApi;
import api.bpartners.annotator.endpoint.rest.api.TasksApi;
import api.bpartners.annotator.endpoint.rest.api.TeamJobsApi;
import api.bpartners.annotator.endpoint.rest.client.ApiClient;
import api.bpartners.annotator.endpoint.rest.client.ApiException;
import api.bpartners.annotator.endpoint.rest.model.AnnotationBaseFields;
import api.bpartners.annotator.endpoint.rest.model.AnnotationNumberPerLabel;
import api.bpartners.annotator.endpoint.rest.model.CreateAnnotatedTask;
import api.bpartners.annotator.endpoint.rest.model.CreateAnnotationBatch;
import api.bpartners.annotator.endpoint.rest.model.CrupdateJob;
import api.bpartners.annotator.endpoint.rest.model.Job;
import api.bpartners.annotator.endpoint.rest.model.Label;
import api.bpartners.annotator.endpoint.rest.model.Task;
import api.bpartners.annotator.endpoint.rest.model.TaskStatistics;
import api.bpartners.annotator.endpoint.rest.model.TaskStatus;
import api.bpartners.annotator.endpoint.rest.security.cognito.CognitoComponent;
import api.bpartners.annotator.integration.conf.utils.TestUtils;
import api.bpartners.annotator.service.aws.JobOrTaskS3Service;
import java.net.MalformedURLException;
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
  @MockBean public JobOrTaskS3Service fileService;

  @BeforeEach
  public void setUp() throws MalformedURLException {
    setUpCognito(cognitoComponent);
    setUpS3Service(fileService);
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
        .labels(crupdateJob.getLabels());
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
            new AnnotationNumberPerLabel().labelName("POOL").numberOfAnnotations(2L),
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

  @Test
  void admin_create_job_then_add_tasks_and_annotator_get_ok() throws ApiException {
    ApiClient adminClient = anAdminApiClient();
    JobsApi api = new JobsApi(adminClient);
    String jobId = randomUUID().toString();
    List<Label> labels = List.of(creatableDummyLabel());
    var payload = crupdateReviewingJob(jobId, labels);
    Job expected =
        from(
            payload,
            new TaskStatistics()
                .completedTasksByUserId(0L)
                .remainingTasks(0L)
                .totalTasks(0L)
                .remainingTasksForUserId(0L));

    Job actual = api.saveJob(jobId, payload);

    String teamId = team1().getId();
    CrupdateJob crupdateJobFromJob = from(actual);
    api.saveJob(jobId, crupdateJobFromJob.status(READY).teamId(teamId));
    api.saveJob(jobId, crupdateJobFromJob.status(STARTED).teamId(teamId));

    assertEquals(expected, actual);
    assertTrue(annotator_can_get_job(teamId, jobId, expected));
    assertTrue(admin_can_add_annotated_tasks(jobId, labels));
    assertTrue(
        annotator_can_get_job(
            teamId,
            jobId,
            expected.taskStatistics(
                new TaskStatistics()
                    .remainingTasks(2L)
                    .totalTasks(2L)
                    .remainingTasksForUserId(2L)
                    .completedTasksByUserId(0L))));
  }

  boolean annotator_can_get_job(String teamId, String jobId, Job expected) throws ApiException {
    ApiClient annotatorClient = anAnnotatorApiClient();
    TeamJobsApi annotatorApi = new TeamJobsApi(annotatorClient);

    Job actualJobFromClientView = annotatorApi.getAnnotatorReadableTeamJobById(teamId, jobId);

    assertEquals(expected.teamId(teamId).status(STARTED), actualJobFromClientView);
    return true;
  }

  boolean admin_can_add_annotated_tasks(String jobId, List<Label> labels) throws ApiException {
    ApiClient apiClient = anAdminApiClient();
    TasksApi tasksApi = new TasksApi(apiClient);
    // refer to EnvConf/tasks.insert.limit.max
    List<CreateAnnotatedTask> actualPayload =
        // this list size is the same as number of added annotations because we only use one label
        // here
        List.of(
            createAnnotatedTask(randomUUID().toString(), labels),
            createAnnotatedTask(randomUUID().toString(), labels));
    List<Task> expectedTasks = actualPayload.stream().map(this::toTask).toList();

    var savedTasks =
        tasksApi.addAnnotatedTasksToAnnotatedJob(jobId, actualPayload).stream()
            .map(this::ignoreTaskIds)
            .toList();

    assertTrue(savedTasks.containsAll(expectedTasks));
    return true;
  }

  @Test
  void export_job_ok() throws ApiException {
    ApiClient apiClient = anAdminApiClient();
    JobsApi jobsApi = new JobsApi(apiClient);
    String okContent = "ok";
    var actual1 = jobsApi.exportJob(JOB_1_ID, VGG, "dummy@gmail.com");
    var actual2 = jobsApi.exportJob(JOB_1_ID, COCO, "dummy@gmail.com");

    verify(eventProducer, times(2)).accept(anyList());
    assertEquals(okContent, actual1);
    assertEquals(okContent, actual2);
  }

  private static CreateAnnotatedTask createAnnotatedTask(String id, List<Label> labels) {
    return new CreateAnnotatedTask()
        .id(id)
        .filename(randomUUID() + ".jpg")
        .annotatorId(GEOJOBS_USER_ID)
        .annotationBatch(
            new CreateAnnotationBatch()
                .id(randomUUID().toString())
                .annotations(
                    List.of(
                        new AnnotationBaseFields()
                            .id(randomUUID().toString())
                            .label(labels.get(0))
                            .polygon(polygon())
                            .userId(GEOJOBS_USER_ID))));
  }

  public static CrupdateJob crupdateReviewingJob(String id, List<Label> labels) {
    return new CrupdateJob()
        .id(id)
        .type(REVIEWING)
        .folderPath("test/")
        .bucketName("dummy")
        .imagesHeight(1024)
        .imagesWidth(1024)
        .teamId(GEOJOBS_TEAM_ID)
        .ownerEmail("admin@gmail.com")
        .status(PENDING)
        .labels(labels)
        .name(id);
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

  Task toTask(CreateAnnotatedTask task) {
    return new Task()
        .id(null)
        .filename(task.getFilename())
        .status(TaskStatus.TO_CORRECT)
        .imageUri(MOCK_PRESIGNED_URL)
        .userId(task.getAnnotatorId());
  }

  Task ignoreTaskIds(Task task) {
    task.setId(null);
    return task;
  }
}
