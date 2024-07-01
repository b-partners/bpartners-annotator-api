package api.bpartners.annotator.unit;

import static api.bpartners.annotator.repository.model.enums.JobStatus.COMPLETED;
import static api.bpartners.annotator.repository.model.enums.JobStatus.PENDING;
import static api.bpartners.annotator.repository.model.enums.JobStatus.STARTED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import api.bpartners.annotator.conf.FacadeIT;
import api.bpartners.annotator.model.exception.BadRequestException;
import api.bpartners.annotator.repository.model.Job;
import api.bpartners.annotator.service.JobService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class JobServiceTest extends FacadeIT {
  @Autowired private JobService subject;

  @Test
  void job_status_started_to_completed_ok() {
    var actual = subject.checkJobStatusTransition(current(), completed());

    assertEquals(COMPLETED, actual);
  }

  @Test
  void job_status_transition_ko() {
    assertThrows(
        BadRequestException.class, () -> subject.checkJobStatusTransition(current(), pending()));
  }

  Job current() {
    return Job.builder().status(STARTED).build();
  }

  Job completed() {
    return Job.builder().status(COMPLETED).build();
  }

  Job pending() {
    return Job.builder().status(PENDING).build();
  }
}
