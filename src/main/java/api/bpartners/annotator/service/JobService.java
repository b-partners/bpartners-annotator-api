package api.bpartners.annotator.service;

import static api.bpartners.annotator.repository.model.enums.JobStatus.PENDING;

import api.bpartners.annotator.endpoint.event.EventProducer;
import api.bpartners.annotator.endpoint.event.gen.JobCreated;
import api.bpartners.annotator.model.exception.BadRequestException;
import api.bpartners.annotator.model.exception.NotFoundException;
import api.bpartners.annotator.repository.jpa.JobRepository;
import api.bpartners.annotator.repository.jpa.LabelRepository;
import api.bpartners.annotator.repository.model.Job;
import api.bpartners.annotator.repository.model.Label;
import api.bpartners.annotator.repository.model.enums.JobStatus;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class JobService {
  private final JobRepository repository;
  private final LabelRepository labelRepository;
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

  public List<Job> getAll() {
    return repository.findAll();
  }

  public Job getById(String id) {
    return repository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Job identified by id = " + id + " not found"));
  }

  @Transactional
  public Job save(Job job) {
    var labels = job.getLabels();
    if (!repository.existsById(job.getId()) && job.getStatus().equals(PENDING)) {
      var savedJob = saveJobAndLabels(job, labels);
      eventProducer.accept(List.of(toEventType(savedJob, null)));
      return savedJob;
    } else {
      return updateJob(job);
    }
  }

  private Job saveJobAndLabels(Job job, List<Label> labels) {
    labelRepository.saveAll(labels);
    return repository.save(job);
  }

  @Transactional
  public Job updateJobStatus(String jobId, JobStatus status) {
    Job persisted = getById(jobId);
    persisted.setStatus(status);
    return updateJob(persisted);
  }

  private Job updateJob(Job job) {
    Job persisted = getById(job.getId());
    checkJobStatusTransition(persisted, job);
    job.setTasks(persisted.getTasks());
    job.setLabels(persisted.getLabels());
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
        case TO_REVIEW, TO_CORRECT -> next;
        case PENDING, READY, STARTED, FAILED, COMPLETED -> throw exception;
      };
    };
  }
}
