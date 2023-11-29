package api.bpartners.annotator.endpoint.rest.controller;

import api.bpartners.annotator.endpoint.rest.controller.mapper.AnnotationMapper;
import api.bpartners.annotator.endpoint.rest.model.Annotation;
import api.bpartners.annotator.service.AnnotationService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class UserAnnotationController {
  private final AnnotationService service;
  private final AnnotationMapper mapper;

  @PutMapping("/users/{userId}/tasks/{taskId}/annotations")
  public List<Annotation> annotateTask(
      @PathVariable String userId,
      @PathVariable String taskId,
      @RequestBody List<Annotation> annotations) {
    var saved = service.save(annotations.stream().map(mapper::toDomain).toList());
    return saved.stream().map(mapper::toRest).toList();
  }
}
