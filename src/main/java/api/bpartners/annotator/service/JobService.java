package api.bpartners.annotator.service;

import api.bpartners.annotator.endpoint.event.EventProducer;
import api.bpartners.annotator.endpoint.event.gen.JobCreated;
import api.bpartners.annotator.endpoint.event.model.TypedJobCreated;
import api.bpartners.annotator.model.exception.NotFoundException;
import api.bpartners.annotator.repository.jpa.JobRepository;
import api.bpartners.annotator.repository.jpa.LabelRepository;
import api.bpartners.annotator.repository.jpa.model.Job;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static api.bpartners.annotator.repository.jpa.model.enums.JobStatus.PENDING;

@Service
@AllArgsConstructor
public class JobService {
  private final JobRepository repository;
  private final LabelRepository labelRepository;
  private final EventProducer eventProducer;

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
    if (!repository.existsById(job.getId()) && job.getStatus().equals(PENDING)) {
      eventProducer.accept(List.of(toTypeEvent(job)));
    }
    labelRepository.saveAll(job.getLabels());
    return repository.save(job);
  }

  private TypedJobCreated toTypeEvent(Job job) {
    return new TypedJobCreated(
        JobCreated.builder()
            .job(job)
            .build()
    );
  }
}
