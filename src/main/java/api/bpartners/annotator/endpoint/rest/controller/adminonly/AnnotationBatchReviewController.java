package api.bpartners.annotator.endpoint.rest.controller.adminonly;

import api.bpartners.annotator.endpoint.rest.controller.mapper.AnnotationBatchReviewMapper;
import api.bpartners.annotator.endpoint.rest.model.AnnotationBatchReview;
import api.bpartners.annotator.service.AnnotationBatchReviewService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class AnnotationBatchReviewController {
  private final AnnotationBatchReviewService service;
  private final AnnotationBatchReviewMapper mapper;

  @GetMapping("/jobs/{jobId}/tasks/{taskId}/annotations/{annotationBatchId}/reviews")
  public List<AnnotationBatchReview> getJobTaskAnnotationReviews(
      @PathVariable String jobId,
      @PathVariable String taskId,
      @PathVariable String annotationBatchId) {
    return service.findAllByJobTaskAndAnnotationBatch(jobId, taskId, annotationBatchId).stream()
        .map(mapper::toRest)
        .toList();
  }

  @GetMapping("/jobs/{jobId}/tasks/{taskId}/annotations/{annotationBatchId}/reviews/{reviewId}")
  public AnnotationBatchReview getJobTaskAnnotationReview(
      @PathVariable String jobId,
      @PathVariable String taskId,
      @PathVariable String annotationBatchId,
      @PathVariable String reviewId) {
    return mapper.toRest(
        service.findByJobTaskAndAnnotationAndId(jobId, taskId, annotationBatchId, reviewId));
  }

  @PutMapping("/jobs/{jobId}/tasks/{taskId}/annotations/{annotationBatchId}/reviews/{reviewId}")
  public AnnotationBatchReview crupdateAnnotationReview(
      @PathVariable String jobId,
      @PathVariable String taskId,
      @PathVariable String annotationBatchId,
      @PathVariable String reviewId,
      @RequestBody AnnotationBatchReview annotationBatchReview) {
    api.bpartners.annotator.repository.model.AnnotationBatchReview saved =
        service.save(taskId, annotationBatchId, mapper.toDomain(annotationBatchReview));
    return mapper.toRest(saved);
  }
}
