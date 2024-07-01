package api.bpartners.annotator.service;

import static api.bpartners.annotator.endpoint.rest.model.JobType.LABELLING;
import static api.bpartners.annotator.endpoint.rest.model.JobType.REVIEWING;
import static api.bpartners.annotator.repository.model.enums.JobStatus.COMPLETED;
import static api.bpartners.annotator.repository.model.enums.JobStatus.PENDING;
import static api.bpartners.annotator.repository.model.enums.JobStatus.STARTED;
import static api.bpartners.annotator.repository.model.enums.JobStatus.TO_CORRECT;
import static api.bpartners.annotator.repository.model.enums.JobStatus.TO_REVIEW;
import static org.springframework.data.domain.Pageable.unpaged;
import static org.springframework.data.domain.Sort.Order.asc;

import api.bpartners.annotator.endpoint.event.EventProducer;
import api.bpartners.annotator.endpoint.event.model.AnnotationStatisticsComputationTriggered;
import api.bpartners.annotator.endpoint.event.model.JobCreated;
import api.bpartners.annotator.endpoint.rest.model.JobType;
import api.bpartners.annotator.model.BoundedPageSize;
import api.bpartners.annotator.model.PageFromOne;
import api.bpartners.annotator.model.exception.BadRequestException;
import api.bpartners.annotator.model.exception.NotFoundException;
import api.bpartners.annotator.repository.dao.JobDao;
import api.bpartners.annotator.repository.jpa.JobRepository;
import api.bpartners.annotator.repository.model.Job;
import api.bpartners.annotator.repository.model.Task;
import api.bpartners.annotator.repository.model.enums.JobStatus;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class JobService {
  private static final Sort JOB_SORT = Sort.by(asc("name"));
  private final JobRepository repository;
  private final JobDao dao;
  private final EventProducer eventProducer;
  private static final List<JobStatus> ANNOTATOR_READABLE_JOB_STATUSES =
      List.of(STARTED, TO_CORRECT);

  public static JobCreated toEventType(Job job, String nextContinuationToken) {
    return JobCreated.builder().nextContinuationToken(nextContinuationToken).job(job).build();
  }

  public List<Job> getAnnotatorReadableJobs(
      String teamId, String name, JobType type, PageFromOne page, BoundedPageSize pageSize) {
    return getAllByTeamAndStatusesAndName(
        teamId, name, type, ANNOTATOR_READABLE_JOB_STATUSES, page, pageSize);
  }

  public List<Job> getAllByTeamAndStatusesAndName(
      String teamId,
      String name,
      JobType type,
      Collection<JobStatus> statuses,
      PageFromOne page,
      BoundedPageSize pageSize) {
    Pageable pageable;
    if (page != null && pageSize != null) {
      pageable = PageRequest.of(page.getValue() - 1, pageSize.getValue(), JOB_SORT);
    } else {
      pageable = unpaged(JOB_SORT);
    }
    return dao.findAllByCriteria(teamId, name, type, statuses, pageable);
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

  public List<Job> getAllByStatusAndName(
      PageFromOne page, BoundedPageSize pageSize, JobType type, JobStatus status, String name) {
    Pageable pageable = PageRequest.of(page.getValue() - 1, pageSize.getValue(), JOB_SORT);
    return dao.findAllByCriteria(
        null, name, type, status == null ? null : List.of(status), pageable);
  }

  public Job getById(String id) {
    return repository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Job identified by id = " + id + " not found"));
  }

  @Transactional
  public Job save(Job job) {
    if (REVIEWING.equals(job.getType())) {
      return repository.save(job);
    } else if (!repository.existsById(job.getId())
        && PENDING.equals(job.getStatus())
        && LABELLING.equals(job.getType())) {
      var savedJob = repository.save(job);
      eventProducer.accept(List.of(toEventType(savedJob, null)));
      return savedJob;
    }
    return updateJob(job);
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
        case READY, STARTED, FAILED -> next;
        case PENDING, TO_REVIEW, TO_CORRECT, COMPLETED -> throw exception;
      };
      case STARTED -> switch (next) {
        case STARTED, TO_REVIEW, TO_CORRECT, FAILED, COMPLETED -> next;
        case PENDING, READY -> throw exception;
      };
      case TO_CORRECT -> switch (next) {
        case TO_CORRECT, STARTED, TO_REVIEW, COMPLETED, FAILED -> next;
        case PENDING, READY -> throw exception;
      };
      case TO_REVIEW -> switch (next) {
        case TO_REVIEW, TO_CORRECT, COMPLETED, FAILED -> next;
        case PENDING, READY, STARTED -> throw exception;
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

  public void fireAnnotationStatisticsComputationEvent(String jobId, String emailCC) {
    InternetAddress cc = null;
    if (emailCC != null) {
      try {
        cc = new InternetAddress(emailCC);
      } catch (AddressException e) {
        throw new BadRequestException("invalid address " + cc);
      }
    }

    eventProducer.accept(List.of(new AnnotationStatisticsComputationTriggered(jobId, cc)));
  }
}
