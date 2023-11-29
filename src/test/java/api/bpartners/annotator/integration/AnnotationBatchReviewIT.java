package api.bpartners.annotator.integration;

import static api.bpartners.annotator.integration.conf.utils.TestMocks.BATCH_1_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.JOB_1_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.REVIEW_1_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.TASK_1_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.annotationReview1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import api.bpartners.annotator.conf.FacadeIT;
import api.bpartners.annotator.endpoint.rest.api.AnnotationsApi;
import api.bpartners.annotator.endpoint.rest.client.ApiClient;
import api.bpartners.annotator.endpoint.rest.client.ApiException;
import api.bpartners.annotator.endpoint.rest.model.AnnotationBatchReview;
import api.bpartners.annotator.integration.conf.utils.TestMocks;
import api.bpartners.annotator.integration.conf.utils.TestUtils;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;

public class AnnotationBatchReviewIT extends FacadeIT {
  @LocalServerPort private int port;

  private ApiClient anApiClient() {
    return TestUtils.anApiClient(null, TestMocks.ADMIN_API_KEY, port);
  }

  @Test
  void get_annotation_reviews_ok() throws ApiException {
    ApiClient adminClient = anApiClient();
    AnnotationsApi api = new AnnotationsApi(adminClient);

    List<AnnotationBatchReview> actual =
        api.getJobTaskAnnotationBatchReviews(JOB_1_ID, TASK_1_ID, BATCH_1_ID);

    assertTrue(actual.contains(annotationReview1()));
  }

  @Test
  void get_annotation_review_ok() throws ApiException {
    ApiClient adminClient = anApiClient();
    AnnotationsApi api = new AnnotationsApi(adminClient);

    AnnotationBatchReview actual =
        api.getJobTaskAnnotationBatchReview(JOB_1_ID, TASK_1_ID, BATCH_1_ID, REVIEW_1_ID);

    assertEquals(annotationReview1(), actual);
  }
}
