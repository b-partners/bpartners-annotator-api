package api.bpartners.annotator.endpoint.rest.controller;

import api.bpartners.annotator.endpoint.rest.controller.mapper.AnnotationBatchReviewMapper;
import api.bpartners.annotator.endpoint.rest.model.AnnotationBatchReview;
import api.bpartners.annotator.service.AnnotationBatchReviewService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class UserAnnotationBatchReviewController {
  private final AnnotationBatchReviewService service;
  private final AnnotationBatchReviewMapper mapper;

  @GetMapping("/users/{userId}/tasks/{taskId}/annotations/{annotationBatchId}/reviews")
  public List<AnnotationBatchReview> getJobTaskAnnotationReviews(
      @PathVariable String userId,
      @PathVariable String taskId,
      @PathVariable String annotationBatchId) {
    return service.findAllByUserTaskAndAnnotation(userId, taskId, annotationBatchId).stream()
        .map(mapper::toRest)
        .toList();
  }

  @GetMapping("/users/{userId}/tasks/{taskId}/annotations/{annotationBatchId}/reviews/{reviewId}")
  public AnnotationBatchReview getJobTaskAnnotationReview(
      @PathVariable String userId,
      @PathVariable String taskId,
      @PathVariable String annotationBatchId,
      @PathVariable String reviewId) {
    return mapper.toRest(
        service.findByUserTaskAndAnnotationAndId(userId, taskId, annotationBatchId, reviewId));
  }
}
