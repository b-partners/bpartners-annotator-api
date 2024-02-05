package api.bpartners.annotator.service;

import static api.bpartners.annotator.repository.model.enums.JobStatus.COMPLETED;
import static api.bpartners.annotator.repository.model.enums.JobStatus.PENDING;
import static api.bpartners.annotator.repository.model.enums.JobStatus.TO_CORRECT;
import static api.bpartners.annotator.repository.model.enums.JobStatus.TO_REVIEW;

import api.bpartners.annotator.endpoint.event.EventProducer;
import api.bpartners.annotator.endpoint.event.gen.AnnotatedJobCrupdated;
import api.bpartners.annotator.endpoint.event.gen.JobCreated;
import api.bpartners.annotator.endpoint.rest.model.CrupdateAnnotatedJob;
import api.bpartners.annotator.model.BoundedPageSize;
import api.bpartners.annotator.model.PageFromOne;
import api.bpartners.annotator.model.exception.BadRequestException;
import api.bpartners.annotator.model.exception.NotFoundException;
import api.bpartners.annotator.repository.jpa.JobRepository;
import api.bpartners.annotator.repository.model.Job;
import api.bpartners.annotator.repository.model.Task;
import api.bpartners.annotator.repository.model.enums.JobStatus;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class JobService {
  private final JobRepository repository;
  private final EventProducer eventProducer;

  public static JobCreated toEventType(Job job, String nextContinuationToken) {
    return JobCreated.builder().nextContinuationToken(nextContinuationToken).job(job).build();
  }

  public List<Job> getAllByTeamAndStatuses(String teamId, Collection<JobStatus> statuses) {
    return repository.findAllByTeamIdAndStatusIn(teamId, statuses);
  }

  public Job getByTeamAndIdAndStatuses(String teamId, String id, Collection<JobStatus> statuses) {
    return repository
        .findByTeamIdAndIdAndStatusIn(teamId, id, statuses)
        .orElseThrow(
            () ->
                new NotFoundException(
                    "Job identified by team.id = "
                        + teamId
                        + " and id = "
                        + id
                        + "with status = oneOf( "
                        + statuses
                        + " ) not found"));
  }

  public List<Job> getAllByStatus(PageFromOne page, BoundedPageSize pageSize, JobStatus status) {
    Pageable pageable = PageRequest.of(page.getValue() - 1, pageSize.getValue());
    if (status == null) {
      return repository.findAll(pageable).toList();
    }
    return repository.findAllByStatus(status, pageable);
  }

  public Job getById(String id) {
    return repository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Job identified by id = " + id + " not found"));
  }

  @Transactional
  public Job save(Job job) {
    if (!repository.existsById(job.getId()) && job.getStatus().equals(PENDING)) {
      var savedJob = repository.save(job);
      eventProducer.accept(List.of(toEventType(savedJob, null)));
      return savedJob;
    }
    return updateJob(job);
  }

  @Transactional
  public Job crupdateAnnotatedJob(
      String jobId, CrupdateAnnotatedJob crupdateAnnotatedJob, Job job) {
    Job updated = repository.save(job);
    eventProducer.accept(List.of(new AnnotatedJobCrupdated(crupdateAnnotatedJob, job)));
    return updated;
  }

  public Job updateJobStatus(String jobId, JobStatus status) {
    Job persisted = getById(jobId);
    persisted.setStatus(status);
    return updateJob(persisted);
  }

  public Job setToReview(String jobId) {
    return updateJobStatus(jobId, TO_REVIEW);
  }

  public Job rejectForCorrection(String jobId) {
    return updateJobStatus(jobId, TO_CORRECT);
  }

  private Job updateJob(Job job) {
    Job persisted = getById(job.getId());
    checkJobStatusTransition(persisted, job);
    job.setLabels(persisted.getLabels());
    job.setTasks(persisted.getTasks());
    job.setBucketName(persisted.getBucketName());
    job.setFolderPath(persisted.getFolderPath());
    return repository.save(job);
  }

  public JobStatus checkJobStatusTransition(Job currentJob, Job newJob) {
    JobStatus current = currentJob.getStatus();
    JobStatus next = newJob.getStatus();
    BadRequestException exception =
        new BadRequestException(String.format("illegal transition: %s -> %s", current, next));
    return switch (current) {
      case PENDING -> switch (next) {
        case PENDING, READY, FAILED -> next;
        case STARTED, TO_REVIEW, TO_CORRECT, COMPLETED -> throw exception;
      };
      case READY -> switch (next) {
        case READY, STARTED -> next;
        case PENDING, FAILED, TO_REVIEW, TO_CORRECT, COMPLETED -> throw exception;
      };
      case STARTED -> switch (next) {
        case STARTED, TO_REVIEW, TO_CORRECT -> next;
        case PENDING, FAILED, READY, COMPLETED -> throw exception;
      };
      case TO_CORRECT -> switch (next) {
        case TO_CORRECT, STARTED, TO_REVIEW, COMPLETED -> next;
        case PENDING, READY, FAILED -> throw exception;
      };
      case TO_REVIEW -> switch (next) {
        case TO_REVIEW, TO_CORRECT, COMPLETED -> next;
        case PENDING, READY, FAILED, STARTED -> throw exception;
      };
      case FAILED -> throw new BadRequestException(
          "Failed Job cannot be changed, create new Job instead");
      case COMPLETED -> switch (next) {
        case COMPLETED, TO_REVIEW, TO_CORRECT -> next;
        case PENDING, READY, STARTED, FAILED -> throw exception;
      };
    };
  }

  @Transactional
  public Job refresh(String jobId) {
    Job persisted = getById(jobId);

    if (persisted.isCompleted()) {
      return persisted;
    }
    List<Task> tasks = persisted.getTasks();
    if (tasks.stream().allMatch(Task::isToReview)) {
      persisted.setStatus(TO_REVIEW);
    }
    if (tasks.stream().allMatch(Task::isCompleted)) {
      persisted.setStatus(COMPLETED);
    }

    return save(persisted);
  }
}
