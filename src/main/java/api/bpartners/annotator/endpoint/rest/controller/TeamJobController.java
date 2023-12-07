package api.bpartners.annotator.endpoint.rest.controller;

import static api.bpartners.annotator.repository.model.enums.JobStatus.STARTED;
import static api.bpartners.annotator.repository.model.enums.JobStatus.TO_CORRECT;

import api.bpartners.annotator.endpoint.rest.controller.mapper.JobMapper;
import api.bpartners.annotator.endpoint.rest.model.Job;
import api.bpartners.annotator.repository.model.enums.JobStatus;
import api.bpartners.annotator.service.JobService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@AllArgsConstructor
public class TeamJobController {
  private final JobService service;
  private final JobMapper mapper;
  private static final List<JobStatus> ANNOTATOR_READABLE_JOB_STATUSES =
      List.of(STARTED, TO_CORRECT);

  @GetMapping("/teams/{teamId}/jobs")
  public List<Job> getAnnotatorReadableTeamJobs(@PathVariable String teamId) {
    return service.getAllByTeamAndStatuses(teamId, ANNOTATOR_READABLE_JOB_STATUSES).stream()
        .map(mapper::toRest)
        .toList();
  }

  @GetMapping("/teams/{teamId}/jobs/{jobId}")
  public Job getAnnotatorReadableTeamJob(@PathVariable String teamId, @PathVariable String jobId) {
    return mapper.toRest(
        service.getByTeamAndIdAndStatuses(teamId, jobId, ANNOTATOR_READABLE_JOB_STATUSES));
  }
}
