package api.bpartners.annotator.unit;

import static api.bpartners.annotator.repository.model.enums.JobStatus.STARTED;
import static org.junit.jupiter.api.Assertions.assertThrows;

import api.bpartners.annotator.conf.FacadeIT;
import api.bpartners.annotator.model.exception.BadRequestException;
import api.bpartners.annotator.repository.model.Job;
import api.bpartners.annotator.service.AnnotatedTaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AnnotatedTaskServiceTest extends FacadeIT {
  @Autowired private AnnotatedTaskService subject;

  @Test
  void add_task_to_started_job_ko() {
    assertThrows(
        BadRequestException.class,
        () -> subject.checkCurrentJobState(startedJob()),
        "cannot add tasks to STARTED Job.");
  }

  Job startedJob() {
    return Job.builder().status(STARTED).build();
  }
}
