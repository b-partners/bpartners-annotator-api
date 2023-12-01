package api.bpartners.annotator.integration;

import static api.bpartners.annotator.integration.conf.utils.TestMocks.ANNOTATION_1_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.BATCH_1_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.BATCH_REVIEW_1_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.JOB_1_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.TASK_1_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.batchReview1;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.batchReview2;
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
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

public class AnnotationBatchReviewIT extends FacadeIT {
  @LocalServerPort private int port;

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
    ApiClient adminClient = anApiClient();
    AnnotationsApi api = new AnnotationsApi(adminClient);
    AnnotationReview review =
        new AnnotationReview().id(UUID.randomUUID().toString()).comment("comment");
    AnnotationReview reviewWithAnnotation =
        new AnnotationReview()
            .id(UUID.randomUUID().toString())
            .comment("comment")
            .annotationId(ANNOTATION_1_ID);

    AnnotationBatchReview actual =
        api.crupdateJobTaskAnnotationReview(
            JOB_1_ID, TASK_1_ID, BATCH_1_ID, BATCH_REVIEW_1_ID, batchReview2());
    AnnotationBatchReview updated =
        api.crupdateJobTaskAnnotationReview(
            JOB_1_ID,
            TASK_1_ID,
            BATCH_1_ID,
            BATCH_REVIEW_1_ID,
            batchReview2().reviews(List.of(review, reviewWithAnnotation)));

    assertEquals(batchReview2(), actual);
    assertEquals(batchReview2().reviews(List.of(review, reviewWithAnnotation)), updated);
  }
}
