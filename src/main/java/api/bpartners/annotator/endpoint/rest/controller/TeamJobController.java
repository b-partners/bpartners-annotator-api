package api.bpartners.annotator.endpoint.rest.controller;

import static api.bpartners.annotator.repository.model.enums.JobStatus.STARTED;
import static api.bpartners.annotator.repository.model.enums.JobStatus.TO_CORRECT;
import static api.bpartners.annotator.repository.model.enums.JobStatus.TO_REVIEW;

import api.bpartners.annotator.endpoint.rest.controller.mapper.JobMapper;
import api.bpartners.annotator.endpoint.rest.model.Job;
import api.bpartners.annotator.endpoint.rest.model.JobType;
import api.bpartners.annotator.model.BoundedPageSize;
import api.bpartners.annotator.model.PageFromOne;
import api.bpartners.annotator.service.JobService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class TeamJobController {
  private final JobService service;
  private final JobMapper mapper;

  @GetMapping("/teams/{teamId}/jobs")
  public List<Job> getAnnotatorReadableTeamJobs(
      @PathVariable String teamId,
      @RequestParam(required = false) String name,
      @RequestParam(required = false) JobType type,
      @RequestParam(required = false) PageFromOne page,
      @RequestParam(required = false) BoundedPageSize pageSize) {
    // TODO(non-blocking): put page and pageSize default values in RequestParam when FrontEnd will
    // use the
    // pagination
    return service.getAnnotatorReadableJobs(teamId, name, type, page, pageSize).stream()
        .map(mapper::toRest)
        .toList();
  }

  @GetMapping("/teams/{teamId}/jobs/{jobId}")
  public Job getAnnotatorReadableTeamJob(@PathVariable String teamId, @PathVariable String jobId) {
    // TODO(non-blocking): fix the FrontEnd bug which needs the TO_REVIEW status after the last job
    // task is completed
    return mapper.toRest(
        service.getByTeamAndIdAndStatuses(teamId, jobId, List.of(STARTED, TO_CORRECT, TO_REVIEW)));
  }
}
