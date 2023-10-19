package api.bpartners.annotator.endpoint.rest.controller;

import api.bpartners.annotator.endpoint.rest.controller.mapper.JobMapper;
import api.bpartners.annotator.endpoint.rest.model.Job;
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

  @GetMapping("/teams/{teamId}/jobs")
  public List<Job> getTeamJobs(@PathVariable String teamId) {
    return service.getAllByTeam(teamId)
        .stream()
        .map(mapper::toRest)
        .toList();
  }
  @GetMapping("/teams/{teamId}/jobs/{jobId}")
  public Job getTeamJob(@PathVariable String teamId, @PathVariable String jobId){
    return mapper.toRest(service.getByTeamAndId(teamId, jobId));
  }
}