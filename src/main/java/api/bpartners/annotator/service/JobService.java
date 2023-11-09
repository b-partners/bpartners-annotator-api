package api.bpartners.annotator.service;

import api.bpartners.annotator.endpoint.event.EventProducer;
import api.bpartners.annotator.endpoint.event.gen.JobCreated;
import api.bpartners.annotator.model.exception.NotFoundException;
import api.bpartners.annotator.repository.jpa.JobRepository;
import api.bpartners.annotator.repository.jpa.LabelRepository;
import api.bpartners.annotator.repository.model.Job;
import api.bpartners.annotator.repository.model.Label;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static api.bpartners.annotator.repository.model.enums.JobStatus.PENDING;

@Service
@AllArgsConstructor
public class JobService {
  private final JobRepository repository;
  private final LabelRepository labelRepository;
  private final EventProducer eventProducer;

  public static JobCreated toEventType(Job job, String nextContinuationToken) {
    return JobCreated.builder()
            .nextContinuationToken(nextContinuationToken)
            .job(job)
            .build();
  }

  public List<Job> getAllByTeam(String teamId) {
    return repository.findAllByTeamId(teamId);
  }

  public Job getByTeamAndId(String teamId, String id) {
    return repository.findByTeamIdAndId(teamId, id)
        .orElseThrow(() -> new NotFoundException(
            "Job identified by team.id = " + teamId + " and id = " + id + " not found"));
  }

  public List<Job> getAll() {
    return repository.findAll();
  }

  public Job getById(String id) {
    return repository.findById(id)
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
      return saveJobAndLabels(job, labels);
    }
  }

  private Job saveJobAndLabels(Job job, List<Label> labels) {
    labelRepository.saveAll(labels);
    return repository.save(job);
  }
}
