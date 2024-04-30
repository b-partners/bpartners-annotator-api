package api.bpartners.annotator.integration;

import static api.bpartners.annotator.integration.conf.utils.TestMocks.BATCH_1_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.BATCH_4_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.JOB_1_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.JOE_DOE_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.TASK_11_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.TASK_1_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.TASK_21_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.annotationBatch1;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.annotationBatch2;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.label1;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.polygon;
import static api.bpartners.annotator.integration.conf.utils.TestUtils.setUpCognito;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import api.bpartners.annotator.conf.FacadeIT;
import api.bpartners.annotator.endpoint.rest.api.AnnotationsApi;
import api.bpartners.annotator.endpoint.rest.api.UserAnnotationsApi;
import api.bpartners.annotator.endpoint.rest.client.ApiClient;
import api.bpartners.annotator.endpoint.rest.client.ApiException;
import api.bpartners.annotator.endpoint.rest.model.Annotation;
import api.bpartners.annotator.endpoint.rest.model.AnnotationBatch;
import api.bpartners.annotator.endpoint.rest.security.cognito.CognitoComponent;
import api.bpartners.annotator.integration.conf.utils.TestMocks;
import api.bpartners.annotator.integration.conf.utils.TestUtils;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class AnnotationIT extends FacadeIT {
  public static final String ANNOTATION_4_ID = "annotation_4_id";
  @LocalServerPort private int port;
  @MockBean CognitoComponent cognitoComponentMock;

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

  private ApiClient janeDoeApiClient() {
    return TestUtils.anApiClient(TestMocks.JANE_DOE_TOKEN, null, port);
  }

  @Test
  void get_all_annotations_by_task_ok() throws ApiException {
    ApiClient adminClient = adminApiClient();
    AnnotationsApi adminApi = new AnnotationsApi(adminClient);
    ApiClient joeDoeClient = joeDoeApiClient();
    UserAnnotationsApi annotatorApi = new UserAnnotationsApi(joeDoeClient);

    List<AnnotationBatch> actual =
        adminApi.getAnnotationBatchesByJobTask(JOB_1_ID, TASK_1_ID, 1, 10);
    List<AnnotationBatch> actualFromAnnotator =
        annotatorApi.getUserTaskAnnotationBatches(JOE_DOE_ID, TASK_11_ID, 1, 100);

    assertTrue(actual.contains(annotationBatch1()));
    assertTrue(actual.contains(annotationBatch2()));
    assertTrue(actualFromAnnotator.contains(annotationBatch4()));
  }

  @Test
  void get_by_id_ok() throws ApiException {
    ApiClient adminClient = adminApiClient();
    AnnotationsApi adminApi = new AnnotationsApi(adminClient);
    ApiClient joeDoeClient = joeDoeApiClient();
    UserAnnotationsApi annotatorApi = new UserAnnotationsApi(joeDoeClient);

    AnnotationBatch actual =
        adminApi.getAnnotationBatchByJobTaskAndId(JOB_1_ID, TASK_1_ID, BATCH_1_ID);
    AnnotationBatch actualFromAnnotator =
        annotatorApi.getUserTaskAnnotationBatchById(JOE_DOE_ID, TASK_11_ID, BATCH_4_ID);

    assertEquals(annotationBatch1(), actual);
    assertEquals(annotationBatch4(), actualFromAnnotator);
  }

  @Test
  void add_annotation_batch_ok() throws ApiException {
    ApiClient joeDoeClient = joeDoeApiClient();
    UserAnnotationsApi annotatorApi = new UserAnnotationsApi(joeDoeClient);
    var payloadId = randomUUID().toString();
    String currentTaskId = TASK_21_ID;
    AnnotationBatch payload =
        new AnnotationBatch()
            .id(payloadId)
            .annotations(
                List.of(
                    new Annotation()
                        .id(randomUUID().toString())
                        .taskId(currentTaskId)
                        .userId(JOE_DOE_ID)
                        .polygon(polygon())
                        .comment(null)
                        .label(label1())))
            .creationDatetime(null);

    var actual =
        annotatorApi.annotateAndSetTaskToReview(JOE_DOE_ID, currentTaskId, payloadId, payload);

    assertEquals(payload, ignoreGeneratedValues(actual));
  }

  private static AnnotationBatch annotationBatch4() {
    return new AnnotationBatch()
        .id(BATCH_4_ID)
        .annotations(List.of(annotation4()))
        .creationDatetime(
            Instant.parse("2023-11-30T20:01:55.907261Z").truncatedTo(ChronoUnit.MILLIS));
  }

  private static Annotation annotation4() {
    return new Annotation()
        .id(ANNOTATION_4_ID)
        .taskId(TASK_11_ID)
        .userId(JOE_DOE_ID)
        .label(label1())
        .polygon(polygon());
  }

  private static AnnotationBatch ignoreGeneratedValues(AnnotationBatch annotationBatch) {
    return annotationBatch.creationDatetime(null);
  }
}
