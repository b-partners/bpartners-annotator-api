package api.bpartners.annotator.service;

import api.bpartners.annotator.repository.jpa.JobRepository;
import api.bpartners.annotator.repository.jpa.LabelRepository;
import api.bpartners.annotator.repository.jpa.model.Job;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class JobService {
  private final JobRepository repository;
  private final LabelRepository labelRepository;

  public List<Job> getAllByTeam(String teamId) {
    return repository.findAllByTeamId(teamId);
  }

  public Job getByTeamAndId(String teamId, String id) {
    return repository.findByTeamIdAndId(teamId, id)
        .orElseThrow(() -> new RuntimeException(
            "Job identified by team.id = " + teamId + " and id = " + id + " not found"));
  }

  public List<Job> getAll() {
    return repository.findAll();
  }

  public Job getById(String id) {
    return repository.findById(id)
        .orElseThrow(() -> new RuntimeException("Job identified by id = " + id + " not found"));
  }

  @Transactional
  public Job save(Job job) {
    labelRepository.saveAll(job.getLabels());
    return repository.save(job);
  }
}
