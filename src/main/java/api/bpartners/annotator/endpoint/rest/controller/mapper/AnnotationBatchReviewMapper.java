package api.bpartners.annotator.endpoint.rest.controller.mapper;

import api.bpartners.annotator.endpoint.rest.model.AnnotationBatchReview;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AnnotationBatchReviewMapper {
  private final ReviewStatusMapper reviewStatusMapper;

  public AnnotationBatchReview toRest(
      api.bpartners.annotator.repository.model.AnnotationBatchReview domain) {
    return new AnnotationBatchReview()
        .id(domain.getId())
        .annotationBatchId(domain.getAnnotationBatchId())
        .annotationId(domain.getAnnotationId())
        .status(reviewStatusMapper.toRest(domain.getStatus()))
        .comment(domain.getComment());
  }

  public api.bpartners.annotator.repository.model.AnnotationBatchReview toDomain(
      AnnotationBatchReview rest) {
    return api.bpartners.annotator.repository.model.AnnotationBatchReview.builder()
        .id(rest.getId())
        .annotationBatchId(rest.getAnnotationBatchId())
        .annotationId(rest.getAnnotationId())
        .status(reviewStatusMapper.toDomain(rest.getStatus()))
        .comment(rest.getComment())
        .build();
  }
}
