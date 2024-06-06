package api.bpartners.annotator.repository.dao;

import static api.bpartners.annotator.integration.conf.utils.TestMocks.NOT_EXISTING_JOB_ID;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.NOT_EXISTING_USER_ID;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import api.bpartners.annotator.conf.FacadeIT;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TaskDaoIT extends FacadeIT {
  @Autowired private TaskDao taskDao;

  @Test
  void findAllByJobIdAndStatusAndUserId_ko() {
    assertThrows(
        AssertionError.class,
        () -> taskDao.findAllByJobIdAndStatusAndUserId(null, null, null, null),
        "JOB ID must not be null");
  }

  @Test
  void findAvailableTaskFromJobOrJobAndUserIdOrJobAndExternalUserIds_empty_ok() {
    var actual =
        taskDao.findAvailableTaskFromJobOrJobAndUserIdOrJobAndExternalUserIds(
            NOT_EXISTING_JOB_ID, NOT_EXISTING_USER_ID, List.of());
    assertTrue(actual.isEmpty());
  }
}
