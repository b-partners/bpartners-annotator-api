package api.bpartners.annotator.endpoint.rest.controller.adminonly;

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
public class AnnotationBatchController {
  private final AnnotationBatchService service;
  private final AnnotationBatchMapper mapper;

  @GetMapping("/jobs/{jobId}/tasks/{taskId}/annotations")
  public List<AnnotationBatch> getAnnotationBatchesByJobTask(
      @PathVariable String jobId,
      @PathVariable String taskId,
      @RequestParam PageFromOne page,
      @RequestParam BoundedPageSize pageSize) {
    return service.findAllByTask(taskId, page, pageSize).stream().map(mapper::toRest).toList();
  }

  @GetMapping("/jobs/{jobId}/tasks/{taskId}/annotations/{annotationBatchId}")
  public AnnotationBatch getAnnotationBatchByJobTaskAndId(
      @PathVariable String jobId,
      @PathVariable String taskId,
      @PathVariable String annotationBatchId) {
    return mapper.toRest(service.findByTaskIdAndId(taskId, annotationBatchId));
  }
}
