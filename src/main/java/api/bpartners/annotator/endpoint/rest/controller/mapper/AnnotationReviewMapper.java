package api.bpartners.annotator.endpoint.rest.controller.mapper;

import api.bpartners.annotator.endpoint.rest.model.AnnotationReview;
import java.time.Instant;
import org.springframework.stereotype.Component;

@Component
public class AnnotationReviewMapper {
  public AnnotationReview toRest(api.bpartners.annotator.repository.model.AnnotationReview domain) {
    return new AnnotationReview()
        .id(domain.getId())
        .annotationId(domain.getAnnotationId())
        .comment(domain.getComment());
  }

  public api.bpartners.annotator.repository.model.AnnotationReview toDomain(
      String annotationBatchReviewId, AnnotationReview annotationReview) {
    return api.bpartners.annotator.repository.model.AnnotationReview.builder()
        .id(annotationReview.getId())
        .annotationId(annotationReview.getAnnotationId())
        .annotationBatchReviewId(annotationBatchReviewId)
        .comment(annotationReview.getComment())
        .creationDatetime(Instant.now())
        .build();
  }
}
