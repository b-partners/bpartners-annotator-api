package api.bpartners.annotator.integration;

import static api.bpartners.annotator.endpoint.rest.model.JobType.LABELLING;
import static api.bpartners.annotator.endpoint.rest.model.ReviewStatus.ACCEPTED;
import static api.bpartners.annotator.endpoint.rest.model.ReviewStatus.REJECTED;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.ANNOTATION_1_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.BATCH_1_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.BATCH_4_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.BATCH_REVIEW_1_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.JOB_1_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.JOE_DOE_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.TASK_11_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.TASK_1_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.TEAM_2_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.batchReview1;
import static api.bpartners.annotator.integration.conf.utils.TestUtils.assertThrowsBadRequestException;
import static api.bpartners.annotator.integration.conf.utils.TestUtils.setUpCognito;
import static api.bpartners.annotator.repository.model.enums.JobStatus.TO_REVIEW;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import api.bpartners.annotator.conf.FacadeIT;
import api.bpartners.annotator.endpoint.event.EventProducer;
import api.bpartners.annotator.endpoint.rest.api.AnnotationsApi;
import api.bpartners.annotator.endpoint.rest.api.UserAnnotationsApi;
import api.bpartners.annotator.endpoint.rest.client.ApiClient;
import api.bpartners.annotator.endpoint.rest.client.ApiException;
import api.bpartners.annotator.endpoint.rest.model.AnnotationBatchReview;
import api.bpartners.annotator.endpoint.rest.model.AnnotationReview;
import api.bpartners.annotator.endpoint.rest.model.ReviewStatus;
import api.bpartners.annotator.endpoint.rest.security.cognito.CognitoComponent;
import api.bpartners.annotator.integration.conf.utils.TestMocks;
import api.bpartners.annotator.integration.conf.utils.TestUtils;
import api.bpartners.annotator.repository.jpa.JobRepository;
import api.bpartners.annotator.repository.jpa.LabelRepository;
import api.bpartners.annotator.repository.jpa.TaskRepository;
import api.bpartners.annotator.repository.model.Job;
import api.bpartners.annotator.repository.model.Label;
import api.bpartners.annotator.repository.model.Task;
import api.bpartners.annotator.repository.model.enums.TaskStatus;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;

class AnnotationBatchReviewIT extends FacadeIT {
  public static final String BATCH_REVIEW_3_ID = "batch_review_3_id";
  @LocalServerPort private int port;
  @Autowired TaskRepository taskRepository;
  @Autowired JobRepository jobRepository;
  @Autowired LabelRepository labelRepository;
  @MockBean CognitoComponent cognitoComponentMock;
  @MockBean EventProducer eventProducerMock;

  @BeforeEach
  void setup() {
    setUpCognito(cognitoComponentMock);
  }

  private ApiClient adminApiClient() {
    return TestUtils.anApiClient(null, TestMocks.ADMIN_API_KEY, port);
  }

  private ApiClient joeDoeApiClient() {
    return TestUtils.anApiClient(TestMocks.JOE_DOE_TOKEN, null, port);
  }

  @Test
  void get_annotation_batch_reviews_ok() throws ApiException {
    ApiClient adminClient = adminApiClient();
    AnnotationsApi adminApi = new AnnotationsApi(adminClient);
    ApiClient joeDoeClient = joeDoeApiClient();
    UserAnnotationsApi annotatorApi = new UserAnnotationsApi(joeDoeClient);

    List<AnnotationBatchReview> actual =
        adminApi.getJobTaskAnnotationBatchReviews(JOB_1_ID, TASK_1_ID, BATCH_1_ID);
    List<AnnotationBatchReview> actualFromAnnotator =
        annotatorApi.getAnnotationReviewsByUserTaskAnnotationBatch(
            JOE_DOE_ID, TASK_11_ID, BATCH_4_ID);

    assertTrue(
        actual.stream()
            .map(AnnotationBatchReviewIT::ignoreDateOf)
            .toList()
            .contains(ignoreDateOf(batchReview1())));
    assertTrue(
        actualFromAnnotator.stream()
            .map(AnnotationBatchReviewIT::ignoreDateOf)
            .toList()
            .contains(ignoreDateOf(batchReview4())));
  }

  @Test
  void get_annotation_batch_review_ok() throws ApiException {
    ApiClient adminClient = adminApiClient();
    AnnotationsApi adminApi = new AnnotationsApi(adminClient);
    ApiClient joeDoeClient = joeDoeApiClient();
    UserAnnotationsApi annotatorApi = new UserAnnotationsApi(joeDoeClient);

    AnnotationBatchReview actual =
        adminApi.getJobTaskAnnotationBatchReview(
            JOB_1_ID, TASK_1_ID, BATCH_1_ID, BATCH_REVIEW_1_ID);
    AnnotationBatchReview actualFromAnnotator =
        annotatorApi.getAnnotationReviewByUserTaskAnnotationBatch(
            JOE_DOE_ID, TASK_11_ID, BATCH_4_ID, BATCH_REVIEW_3_ID);

    assertEquals(ignoreDateOf(batchReview1()), ignoreDateOf(actual));
    assertEquals(ignoreDateOf(batchReview4()), ignoreDateOf(actualFromAnnotator));
  }

  @Test
  void crupdate_rejected_annotation_batch_review_ok() throws ApiException {
    Task createdTask = createTask(createJob(createLabel()));
    ApiClient adminClient = adminApiClient();
    AnnotationsApi api = new AnnotationsApi(adminClient);
    AnnotationReview review = new AnnotationReview().id(randomUUID().toString()).comment("comment");
    AnnotationReview reviewWithAnnotation =
        new AnnotationReview()
            .id(randomUUID().toString())
            .comment("comment")
            .annotationId(ANNOTATION_1_ID);
    String currentBatchId = BATCH_1_ID;
    AnnotationBatchReview toCreatePayload =
        creatableReview(currentBatchId, REJECTED, List.of(review, reviewWithAnnotation));

    AnnotationBatchReview actual =
        api.crupdateJobTaskAnnotationReview(
            JOB_1_ID,
            createdTask.getId(),
            currentBatchId,
            toCreatePayload.getId(),
            toCreatePayload);
    AnnotationBatchReview updatedPayload =
        toCreatePayload.reviews(List.of(review, reviewWithAnnotation));
    AnnotationBatchReview updated =
        api.crupdateJobTaskAnnotationReview(
            JOB_1_ID, createdTask.getId(), currentBatchId, toCreatePayload.getId(), updatedPayload);

    assertEquals(ignoreDateOf(toCreatePayload), ignoreDateOf(actual));
    assertEquals(ignoreDateOf(updatedPayload), ignoreDateOf(updated));
  }

  @Test
  void crupdate_accepted_annotation_batch_review_ok() throws ApiException {
    Task createdTask = createTask(createJob(createLabel()));
    ApiClient adminClient = adminApiClient();
    AnnotationsApi api = new AnnotationsApi(adminClient);
    AnnotationReview review = new AnnotationReview().id(randomUUID().toString()).comment("comment");
    AnnotationReview reviewWithAnnotation =
        new AnnotationReview()
            .id(randomUUID().toString())
            .comment("comment")
            .annotationId(ANNOTATION_1_ID);
    String currentBatchId = BATCH_1_ID;
    AnnotationBatchReview toCreatePayload =
        creatableReview(currentBatchId, ACCEPTED, List.of(review, reviewWithAnnotation));

    AnnotationBatchReview actual =
        api.crupdateJobTaskAnnotationReview(
            JOB_1_ID,
            createdTask.getId(),
            currentBatchId,
            toCreatePayload.getId(),
            toCreatePayload);
    AnnotationBatchReview updatedPayload = resetReviews(toCreatePayload);
    AnnotationBatchReview updated =
        api.crupdateJobTaskAnnotationReview(
            JOB_1_ID, createdTask.getId(), currentBatchId, toCreatePayload.getId(), updatedPayload);

    assertEquals(ignoreDateOf(toCreatePayload), ignoreDateOf(actual));
    assertEquals(ignoreDateOf(updatedPayload), ignoreDateOf(updated));
  }

  private static AnnotationBatchReview resetReviews(AnnotationBatchReview originalReview) {
    return new AnnotationBatchReview()
        .reviews(List.of())
        .annotationBatchId(originalReview.getAnnotationBatchId())
        .id(originalReview.getId())
        .status(originalReview.getStatus());
  }

  @Test
  void crupdate_annotation_batch_review_ko() {
    ApiClient adminClient = adminApiClient();
    AnnotationsApi api = new AnnotationsApi(adminClient);
    String currentBatchId = BATCH_1_ID;
    AnnotationBatchReview toCreatePayload = creatableReview(currentBatchId, REJECTED, List.of());

    assertThrowsBadRequestException(
        () ->
            api.crupdateJobTaskAnnotationReview(
                JOB_1_ID, TASK_1_ID, currentBatchId, toCreatePayload.getId(), toCreatePayload),
        "Reviews are mandatory for rejected batch review " + toCreatePayload.getId());
  }

  private Task createTask(Job job) {
    return taskRepository.save(
        Task.builder()
            .id(randomUUID().toString())
            .job(job)
            .userId(JOE_DOE_ID)
            .filename(randomUUID().toString())
            .status(TaskStatus.TO_REVIEW)
            .build());
  }

  private Job createJob(Label label) {
    return jobRepository.save(
        Job.builder()
            .id(randomUUID().toString())
            .bucketName(randomUUID().toString())
            .ownerEmail(randomUUID().toString())
            .status(TO_REVIEW)
            .name(randomUUID().toString())
            .teamId(TEAM_2_ID)
            .labels(List.of(label))
            .type(LABELLING)
            .build());
  }

  private Label createLabel() {
    return labelRepository.save(
        Label.builder()
            .id(randomUUID().toString())
            .color("#123232")
            .name(randomUUID().toString())
            .build());
  }

  private static AnnotationBatchReview creatableReview(
      String batchId, ReviewStatus status, List<AnnotationReview> reviews) {
    return new AnnotationBatchReview()
        .id(randomUUID().toString())
        .reviews(reviews)
        .status(status)
        .annotationBatchId(batchId);
  }

  private static AnnotationBatchReview batchReview4() {
    return new AnnotationBatchReview()
        .id(BATCH_REVIEW_3_ID)
        .annotationBatchId(BATCH_4_ID)
        .status(REJECTED)
        .reviews(List.of(annotationReview4()));
  }

  private static AnnotationReview annotationReview4() {
    return new AnnotationReview()
        .id("review_2_id")
        .annotationId("annotation_4_id")
        .comment("remove points");
  }

  private static AnnotationBatchReview ignoreDateOf(AnnotationBatchReview review) {
    return review.creationDatetime(null);
  }
}
