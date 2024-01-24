package api.bpartners.annotator.endpoint.rest.controller;

import static api.bpartners.annotator.repository.model.enums.JobStatus.STARTED;
import static api.bpartners.annotator.repository.model.enums.JobStatus.TO_CORRECT;
import static api.bpartners.annotator.repository.model.enums.JobStatus.TO_REVIEW;

import api.bpartners.annotator.endpoint.rest.controller.mapper.JobMapper;
import api.bpartners.annotator.endpoint.rest.model.Job;
import api.bpartners.annotator.endpoint.rest.model.JobType;
import api.bpartners.annotator.model.BoundedPageSize;
import api.bpartners.annotator.model.PageFromOne;
import api.bpartners.annotator.repository.model.enums.JobStatus;
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
  private static final List<JobStatus> ANNOTATOR_READABLE_JOB_STATUSES =
      List.of(STARTED, TO_CORRECT);

  @GetMapping("/teams/{teamId}/jobs")
  public List<Job> getAnnotatorReadableTeamJobs(
      @PathVariable String teamId,
      @RequestParam(required = false) String name,
      @RequestParam(required = false) JobType type,
      @RequestParam(required = false, defaultValue = "1") PageFromOne page,
      @RequestParam(required = false, defaultValue = "10") BoundedPageSize pageSize) {
    return service
        .getAllByTeamAndStatusesAndName(
            teamId, name, type, ANNOTATOR_READABLE_JOB_STATUSES, page, pageSize)
        .stream()
        .map(mapper::toRest)
        .toList();
  }

  @GetMapping("/teams/{teamId}/jobs/{jobId}")
  public Job getAnnotatorReadableTeamJob(@PathVariable String teamId, @PathVariable String jobId) {
    return mapper.toRest(
        service.getByTeamAndIdAndStatuses(teamId, jobId, List.of(STARTED, TO_CORRECT, TO_REVIEW)));
  }
}
