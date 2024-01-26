package api.bpartners.annotator.endpoint.rest.controller.adminOnly;

import static java.util.stream.Collectors.toList;

import api.bpartners.annotator.endpoint.rest.controller.mapper.AnnotationBatchMapper;
import api.bpartners.annotator.endpoint.rest.model.AnnotationBatch;
import api.bpartners.annotator.endpoint.rest.security.model.Principal;
import api.bpartners.annotator.endpoint.rest.validator.AnnotationBatchIdValidator;
import api.bpartners.annotator.model.BoundedPageSize;
import api.bpartners.annotator.model.PageFromOne;
import api.bpartners.annotator.repository.model.User;
import api.bpartners.annotator.service.AnnotationBatchService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class AnnotationBatchController {
  private final AnnotationBatchService service;
  private final AnnotationBatchMapper mapper;
  private final AnnotationBatchIdValidator idValidator;

  @GetMapping("/jobs/{jobId}/tasks/{taskId}/annotations")
  public List<AnnotationBatch> getAnnotationBatchesByJobTask(
      @PathVariable String jobId,
      @PathVariable String taskId,
      @RequestParam PageFromOne page,
      @RequestParam BoundedPageSize pageSize) {
    return service.findAllByTask(taskId, page, pageSize).stream()
        .map(mapper::toRest)
        .collect(toList());
  }

  @GetMapping("/jobs/{jobId}/tasks/{taskId}/annotations/{annotationBatchId}")
  public AnnotationBatch getAnnotationBatchByJobTaskAndId(
      @PathVariable String jobId,
      @PathVariable String taskId,
      @PathVariable String annotationBatchId) {
    return mapper.toRest(service.findByTaskIdAndId(taskId, annotationBatchId));
  }

  @PutMapping("/jobs/{jobId}/tasks/{taskId}/annotations/{annotationBatchId}")
  public AnnotationBatch annotateAndCompleteTask(
      @PathVariable String jobId,
      @PathVariable String taskId,
      @PathVariable String annotationBatchId,
      @RequestBody AnnotationBatch annotationBatch,
      @AuthenticationPrincipal Principal principal) {
    idValidator.accept(annotationBatch, annotationBatchId);
    User connectedUser = principal.getUser();
    String connectedUserId = connectedUser.getId();
    return mapper.toRest(
        service.annotateAndCompleteAnyTask(
            connectedUser, mapper.toDomain(connectedUserId, taskId, annotationBatch)));
  }
}
