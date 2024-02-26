package api.bpartners.annotator.endpoint.rest.controller.adminOnly;

import api.bpartners.annotator.endpoint.rest.controller.mapper.TaskMapper;
import api.bpartners.annotator.endpoint.rest.controller.mapper.TaskStatusMapper;
import api.bpartners.annotator.endpoint.rest.model.CreateAnnotatedTask;
import api.bpartners.annotator.endpoint.rest.model.Task;
import api.bpartners.annotator.endpoint.rest.model.TaskStatus;
import api.bpartners.annotator.model.BoundedPageSize;
import api.bpartners.annotator.model.PageFromOne;
import api.bpartners.annotator.service.AnnotatedTaskService;
import api.bpartners.annotator.service.TaskService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@AllArgsConstructor
public class TaskController {
  private final TaskService service;
  private final AnnotatedTaskService annotatedTaskService;
  private final TaskMapper mapper;
  private final TaskStatusMapper statusMapper;

  @GetMapping("/jobs/{jobId}/tasks")
  public List<Task> getTasks(
      @PathVariable String jobId,
      @RequestParam(required = false, defaultValue = "1") PageFromOne page,
      @RequestParam(required = false, defaultValue = "30") BoundedPageSize pageSize,
      @RequestParam(required = false) TaskStatus status,
      @RequestParam(required = false) String userId) {
    return service
        .getAllByJobAndStatus(jobId, statusMapper.toDomain(status), userId, page, pageSize)
        .stream()
        .map(mapper::toRest)
        .toList();
  }

  @GetMapping("/jobs/{jobId}/tasks/{taskId}")
  public Task getTask(@PathVariable String jobId, @PathVariable String taskId) {
    return mapper.toRest(service.getByJobIdAndId(jobId, taskId));
  }

  @PutMapping("/jobs/{jobId}/tasks")
  public List<Task> addTasksToAnnotatedJob(
      @PathVariable String jobId, @RequestBody List<CreateAnnotatedTask> annotatedTasks) {
    return annotatedTaskService.saveAll(jobId, annotatedTasks).stream()
        .map(mapper::toRest)
        .toList();
  }
}
