package api.bpartners.annotator.endpoint.rest.controller;

import api.bpartners.annotator.endpoint.rest.controller.mapper.TaskMapper;
import api.bpartners.annotator.endpoint.rest.model.Task;
import api.bpartners.annotator.model.BoundedPageSize;
import api.bpartners.annotator.model.PageFromOne;
import api.bpartners.annotator.service.TaskService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@AllArgsConstructor
public class TaskController {
  private final TaskService service;
  private final TaskMapper mapper;

  @GetMapping("/jobs/{jobId}/tasks")
  public List<Task> getTasks(@PathVariable String jobId,
                             @RequestParam(required = false) PageFromOne page,
                             @RequestParam(required = false) BoundedPageSize pageSize) {
    return service.getAllByJob(jobId, page, pageSize).stream().map(mapper::toRest).toList();
  }

  @GetMapping("/jobs/{jobId}/tasks/{taskId}")
  public Task getTask(@PathVariable String jobId, @PathVariable String taskId) {
    return mapper.toRest(service.getByJobIdAndId(jobId, taskId));
  }
}
