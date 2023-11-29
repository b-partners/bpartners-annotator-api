package api.bpartners.annotator.integration;

import static api.bpartners.annotator.integration.conf.utils.TestMocks.ANNOTATION_1_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.JOB_1_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.TASK_1_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.annotation1;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.annotation2;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import api.bpartners.annotator.conf.FacadeIT;
import api.bpartners.annotator.endpoint.rest.api.AnnotationsApi;
import api.bpartners.annotator.endpoint.rest.client.ApiClient;
import api.bpartners.annotator.endpoint.rest.client.ApiException;
import api.bpartners.annotator.endpoint.rest.model.Annotation;
import api.bpartners.annotator.integration.conf.utils.TestMocks;
import api.bpartners.annotator.integration.conf.utils.TestUtils;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class AnnotationIT extends FacadeIT {
  @LocalServerPort private int port;

  private ApiClient anApiClient() {
    return TestUtils.anApiClient(null, TestMocks.ADMIN_API_KEY, port);
  }

  @Test
  void get_all_annotations_by_task_ok() throws ApiException {
    ApiClient adminClient = anApiClient();
    AnnotationsApi api = new AnnotationsApi(adminClient);

    List<Annotation> actual = api.getAnnotationsByJobTask(JOB_1_ID, TASK_1_ID, 1, 10);

    assertTrue(actual.contains(annotation1()));
    assertTrue(actual.contains(annotation2()));
  }

  @Test
  void get_by_id_ok() throws ApiException {
    ApiClient adminClient = anApiClient();
    AnnotationsApi api = new AnnotationsApi(adminClient);

    Annotation actual = api.getAnnotationByJobTaskAndId(JOB_1_ID, TASK_1_ID, ANNOTATION_1_ID);

    assertEquals(annotation1(), actual);
  }
}
