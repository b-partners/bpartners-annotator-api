package api.bpartners.annotator.endpoint.rest.controller;

import api.bpartners.annotator.endpoint.rest.controller.mapper.AnnotationBatchMapper;
import api.bpartners.annotator.endpoint.rest.model.AnnotationBatch;
import api.bpartners.annotator.endpoint.rest.validator.AnnotationBatchIdValidator;
import api.bpartners.annotator.service.AnnotationBatchService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class UserAnnotationController {
  private final AnnotationBatchService service;
  private final AnnotationBatchMapper mapper;
  private final AnnotationBatchIdValidator idValidator;

  @PutMapping("/users/{userId}/tasks/{taskId}/annotations/{annotationBatchId}")
  public AnnotationBatch annotateTask(
      @PathVariable String userId,
      @PathVariable String taskId,
      @PathVariable String annotationBatchId,
      @RequestBody AnnotationBatch annotationBatch) {
    idValidator.accept(annotationBatch, annotationBatchId);
    return mapper.toRest(service.save(mapper.toDomain(userId, taskId, annotationBatch)));
  }
}
