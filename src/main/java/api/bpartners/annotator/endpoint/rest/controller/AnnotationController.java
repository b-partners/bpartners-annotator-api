package api.bpartners.annotator.endpoint.rest.controller;

import static java.util.stream.Collectors.toList;

import api.bpartners.annotator.endpoint.rest.controller.mapper.AnnotationMapper;
import api.bpartners.annotator.endpoint.rest.model.Annotation;
import api.bpartners.annotator.model.BoundedPageSize;
import api.bpartners.annotator.model.PageFromOne;
import api.bpartners.annotator.service.AnnotationService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class AnnotationController {
  private final AnnotationService service;
  private final AnnotationMapper mapper;

  @GetMapping("/jobs/{jobId}/tasks/{taskId}/annotations")
  public List<Annotation> getAnnotationsByJobTask(
      @PathVariable String jobId,
      @PathVariable String taskId,
      @RequestParam PageFromOne page,
      @RequestParam BoundedPageSize pageSize) {
    return service.findAllByTask(taskId, page, pageSize).stream()
        .map(mapper::toRest)
        .collect(toList());
  }

  @GetMapping("/jobs/{jobId}/tasks/{taskId}/annotations/{annotationId}")
  public Annotation getAnnotationByJobTaskAndId(
      @PathVariable String jobId, @PathVariable String taskId, @PathVariable String annotationId) {
    return mapper.toRest(service.findByTaskIdAndId(taskId, annotationId));
  }
}
