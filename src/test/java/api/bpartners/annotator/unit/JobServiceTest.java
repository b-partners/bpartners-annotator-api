package api.bpartners.annotator.unit;

import static api.bpartners.annotator.repository.model.enums.JobStatus.COMPLETED;
import static api.bpartners.annotator.repository.model.enums.JobStatus.PENDING;
import static api.bpartners.annotator.repository.model.enums.JobStatus.STARTED;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import api.bpartners.annotator.conf.FacadeIT;
import api.bpartners.annotator.endpoint.event.EventProducer;
import api.bpartners.annotator.endpoint.event.model.GeoJobsNotificationSent;
import api.bpartners.annotator.model.exception.BadRequestException;
import api.bpartners.annotator.repository.jpa.JobRepository;
import api.bpartners.annotator.repository.model.Job;
import api.bpartners.annotator.repository.model.Task;
import api.bpartners.annotator.repository.model.enums.TaskStatus;
import api.bpartners.annotator.service.JobService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class JobServiceTest extends FacadeIT {
  @Autowired private JobService subject;
  @MockBean private JobRepository repositoryMock;
  @MockBean private EventProducer eventProducerMock;

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

  @Test
  void refresh_job_status() {
    String jobId = randomUUID().toString();
    when(repositoryMock.findById(any()))
        .thenReturn(
            Optional.of(
                Job.builder()
                    .id(jobId)
                    .tasks(List.of(Task.builder().status(TaskStatus.COMPLETED).build()))
                    .build()));
    when(repositoryMock.save(any())).thenReturn(Job.builder().id(jobId).build());
    var eventCapture = ArgumentCaptor.forClass(List.class);

    subject.refresh(jobId);
    verify(eventProducerMock, times(1)).accept(eventCapture.capture());
    var event = eventCapture.getValue().getFirst();

    assertEquals(new GeoJobsNotificationSent(jobId), event);
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
