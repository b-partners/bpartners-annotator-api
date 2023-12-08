package api.bpartners.annotator.endpoint.rest.controller;

import api.bpartners.annotator.endpoint.rest.controller.mapper.TaskMapper;
import api.bpartners.annotator.endpoint.rest.model.Task;
import api.bpartners.annotator.endpoint.rest.model.UpdateTask;
import api.bpartners.annotator.endpoint.rest.security.model.Principal;
import api.bpartners.annotator.service.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class UserTaskController {
  private final TaskService service;
  private final TaskMapper mapper;

  @GetMapping("/teams/{teamId}/jobs/{jobId}/task")
  public Task getUserTaskByJob(
      @PathVariable String teamId,
      @PathVariable String jobId,
      @AuthenticationPrincipal Principal principal) {
    return mapper.toRest(
        service.getAvailableTaskFromJobOrJobAndUserId(teamId, jobId, principal.getUser().getId()));
  }

  @PutMapping("/teams/{teamId}/jobs/{jobId}/tasks/{taskId}")
  public Task updateTask(
      @PathVariable String teamId,
      @PathVariable String jobId,
      @PathVariable String taskId,
      @RequestBody UpdateTask task) {
    return mapper.toRest(service.update(jobId, taskId, mapper.toDomain(task)));
  }
}
