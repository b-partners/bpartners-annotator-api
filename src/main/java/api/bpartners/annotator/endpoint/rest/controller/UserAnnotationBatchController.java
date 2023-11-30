package api.bpartners.annotator.endpoint.rest.controller;

import static java.util.stream.Collectors.toList;

import api.bpartners.annotator.endpoint.rest.controller.mapper.AnnotationBatchMapper;
import api.bpartners.annotator.endpoint.rest.model.AnnotationBatch;
import api.bpartners.annotator.model.BoundedPageSize;
import api.bpartners.annotator.model.PageFromOne;
import api.bpartners.annotator.service.AnnotationBatchService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class UserAnnotationBatchController {
  private final AnnotationBatchService service;
  private final AnnotationBatchMapper mapper;

  @GetMapping("/users/{userId}/tasks/{taskId}/annotations")
  public List<AnnotationBatch> getAnnotationBatchesByJobTask(
      @PathVariable String userId,
      @PathVariable String taskId,
      @RequestParam PageFromOne page,
      @RequestParam BoundedPageSize pageSize) {
    return service.findAllByAnnotatorIdAndTask(userId, taskId, page, pageSize).stream()
        .map(mapper::toRest)
        .collect(toList());
  }

  @GetMapping("/users/{userId}/tasks/{taskId}/annotations/{annotationBatchId}")
  public AnnotationBatch getAnnotationBatchByJobTaskAndId(
      @PathVariable String userId,
      @PathVariable String taskId,
      @PathVariable String annotationBatchId) {
    return mapper.toRest(
        service.findByAnnotatorIdAndTaskIdAndId(userId, taskId, annotationBatchId));
  }
}
