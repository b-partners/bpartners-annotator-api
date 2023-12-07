package api.bpartners.annotator.integration;

import static api.bpartners.annotator.endpoint.rest.model.TaskStatus.*;
import static api.bpartners.annotator.endpoint.rest.model.TaskStatus.PENDING;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.JANE_DOE_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.JOB_1_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.JOE_DOE_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.TASK_1_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.task1;
import static api.bpartners.annotator.integration.conf.utils.TestUtils.setUpS3Service;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import api.bpartners.annotator.conf.FacadeIT;
import api.bpartners.annotator.endpoint.rest.api.TasksApi;
import api.bpartners.annotator.endpoint.rest.client.ApiClient;
import api.bpartners.annotator.endpoint.rest.client.ApiException;
import api.bpartners.annotator.endpoint.rest.model.Task;
import api.bpartners.annotator.integration.conf.utils.TestMocks;
import api.bpartners.annotator.integration.conf.utils.TestUtils;
import api.bpartners.annotator.service.aws.S3Service;
import java.net.MalformedURLException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class TaskIT extends FacadeIT {
  @LocalServerPort private int port;
  @MockBean public S3Service fileService;

  private ApiClient anApiClient() {
    return TestUtils.anApiClient(null, TestMocks.ADMIN_API_KEY, port);
  }

  @BeforeEach
  void setUp() throws MalformedURLException {
    setUpS3Service(fileService);
  }

  @Test
  void admin_get_tasks_ok() throws ApiException {
    ApiClient adminClient = anApiClient();
    TasksApi api = new TasksApi(adminClient);

    List<Task> actualTasks = api.getJobTasks(JOB_1_ID, 1, 20, null, null);

    assertEquals(14, actualTasks.size());
    assertTrue(actualTasks.contains(task1()));
  }

  @Test
  void admin_get_tasks_filtered_ok() throws ApiException {
    ApiClient adminClient = anApiClient();
    TasksApi api = new TasksApi(adminClient);

    List<Task> actualPendingTasks = api.getJobTasks(JOB_1_ID, 1, 10, PENDING, null);
    List<Task> actualUnderCompletionTasks =
        api.getJobTasks(JOB_1_ID, 1, 10, UNDER_COMPLETION, null);
    List<Task> actualToCorrectTasks = api.getJobTasks(JOB_1_ID, 1, 10, TO_CORRECT, null);
    List<Task> actualCompletedTasks = api.getJobTasks(JOB_1_ID, 1, 10, COMPLETED, null);
    List<Task> actualJoeTasks = api.getJobTasks(JOB_1_ID, 1, 10, null, JOE_DOE_ID);
    List<Task> actualCompletedJaneTasks = api.getJobTasks(JOB_1_ID, 1, 10, COMPLETED, JANE_DOE_ID);
    List<Task> actualPendingJaneTasks = api.getJobTasks(JOB_1_ID, 1, 10, PENDING, JANE_DOE_ID);

    // status only filter
    assertEquals(9, actualPendingTasks.size());
    assertEquals(2, actualUnderCompletionTasks.size());
    assertEquals(2, actualToCorrectTasks.size());
    assertEquals(1, actualCompletedTasks.size());

    // userId only filter
    assertEquals(2, actualJoeTasks.size());
    assertTrue(actualJoeTasks.stream().allMatch(j -> JOE_DOE_ID.equals(j.getUserId())));

    // both filters
    assertEquals(1, actualCompletedJaneTasks.size());
    assertEquals(0, actualPendingJaneTasks.size());
    assertTrue(actualCompletedJaneTasks.stream().allMatch(j -> JANE_DOE_ID.equals(j.getUserId())));
  }

  @Test
  void admin_get_task_by_id() throws ApiException {
    ApiClient adminClient = anApiClient();
    TasksApi api = new TasksApi(adminClient);

    Task actual = api.getJobTaskById(JOB_1_ID, TASK_1_ID);

    assertEquals(task1(), actual);
  }
}
