package api.bpartners.annotator.integration;

import static api.bpartners.annotator.endpoint.rest.model.JobType.LABELLING;
import static api.bpartners.annotator.endpoint.rest.model.ReviewStatus.REJECTED;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.ANNOTATION_1_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.BATCH_1_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.BATCH_2_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.BATCH_REVIEW_1_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.JOB_1_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.JOE_DOE_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.TASK_1_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.TEAM_2_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.batchReview1;
import static api.bpartners.annotator.repository.model.enums.JobStatus.TO_REVIEW;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import api.bpartners.annotator.conf.FacadeIT;
import api.bpartners.annotator.endpoint.rest.api.AnnotationsApi;
import api.bpartners.annotator.endpoint.rest.client.ApiClient;
import api.bpartners.annotator.endpoint.rest.client.ApiException;
import api.bpartners.annotator.endpoint.rest.model.AnnotationBatchReview;
import api.bpartners.annotator.endpoint.rest.model.AnnotationReview;
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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

public class AnnotationBatchReviewIT extends FacadeIT {
  @LocalServerPort private int port;
  @Autowired TaskRepository taskRepository;
  @Autowired JobRepository jobRepository;
  @Autowired LabelRepository labelRepository;

  private ApiClient anApiClient() {
    return TestUtils.anApiClient(null, TestMocks.ADMIN_API_KEY, port);
  }

  @Test
  void get_annotation_batch_reviews_ok() throws ApiException {
    ApiClient adminClient = anApiClient();
    AnnotationsApi api = new AnnotationsApi(adminClient);

    List<AnnotationBatchReview> actual =
        api.getJobTaskAnnotationBatchReviews(JOB_1_ID, TASK_1_ID, BATCH_1_ID);

    assertTrue(actual.contains(batchReview1()));
  }

  @Test
  void get_annotation_batch_review_ok() throws ApiException {
    ApiClient adminClient = anApiClient();
    AnnotationsApi api = new AnnotationsApi(adminClient);

    AnnotationBatchReview actual =
        api.getJobTaskAnnotationBatchReview(JOB_1_ID, TASK_1_ID, BATCH_1_ID, BATCH_REVIEW_1_ID);

    assertEquals(batchReview1(), actual);
  }

  @Test
  @DirtiesContext
  void crupdate_annotation_batch_review_ok() throws ApiException {
    Task createdTask = createTask(createJob(createLabel()));
    ApiClient adminClient = anApiClient();
    AnnotationsApi api = new AnnotationsApi(adminClient);
    AnnotationReview review = new AnnotationReview().id(randomUUID().toString()).comment("comment");
    AnnotationReview reviewWithAnnotation =
        new AnnotationReview()
            .id(randomUUID().toString())
            .comment("comment")
            .annotationId(ANNOTATION_1_ID);
    AnnotationBatchReview toCreatePayload =
        creatableRejectedReview(List.of(review, reviewWithAnnotation));

    AnnotationBatchReview actual =
        api.crupdateJobTaskAnnotationReview(
            JOB_1_ID, createdTask.getId(), BATCH_1_ID, toCreatePayload.getId(), toCreatePayload);
    AnnotationBatchReview updatedPayload =
        toCreatePayload.reviews(List.of(review, reviewWithAnnotation));
    AnnotationBatchReview updated =
        api.crupdateJobTaskAnnotationReview(
            JOB_1_ID, createdTask.getId(), BATCH_1_ID, toCreatePayload.getId(), updatedPayload);

    assertEquals(ignoreDateOf(toCreatePayload), ignoreDateOf(actual));
    assertEquals(ignoreDateOf(updatedPayload), ignoreDateOf(updated));
  }

  Task createTask(Job job) {
    return taskRepository.save(
        Task.builder()
            .id(randomUUID().toString())
            .job(job)
            .userId(JOE_DOE_ID)
            .filename(randomUUID().toString())
            .status(TaskStatus.TO_REVIEW)
            .build());
  }

  Job createJob(Label label) {
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

  Label createLabel() {
    return labelRepository.save(
        Label.builder()
            .id(randomUUID().toString())
            .color("#123232")
            .name(randomUUID().toString())
            .build());
  }

  AnnotationBatchReview creatableRejectedReview(List<AnnotationReview> reviews) {
    return new AnnotationBatchReview()
        .id(randomUUID().toString())
        .reviews(reviews)
        .status(REJECTED)
        .annotationBatchId(BATCH_2_ID);
  }

  private static AnnotationBatchReview ignoreDateOf(AnnotationBatchReview review) {
    return review.creationDatetime(null);
  }
}
