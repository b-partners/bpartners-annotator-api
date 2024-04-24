package api.bpartners.annotator.endpoint.rest.controller.mapper;

import api.bpartners.annotator.endpoint.rest.model.AnnotationBatchReview;
import api.bpartners.annotator.endpoint.rest.model.AnnotationReview;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AnnotationBatchReviewMapper {
  private final ReviewStatusMapper reviewStatusMapper;
  private final AnnotationReviewMapper reviewMapper;

  public AnnotationBatchReview toRest(
      api.bpartners.annotator.repository.model.AnnotationBatchReview domain) {
    List<AnnotationReview> reviews =
        domain.getReviews().stream().map(reviewMapper::toRest).toList();
    return new AnnotationBatchReview()
        .id(domain.getId())
        .annotationBatchId(domain.getAnnotationBatchId())
        .status(reviewStatusMapper.toRest(domain.getStatus()))
        .creationDatetime(domain.getCreationDatetime())
        .reviews(reviews);
  }

  public api.bpartners.annotator.repository.model.AnnotationBatchReview toDomain(
      AnnotationBatchReview rest) {
    List<api.bpartners.annotator.repository.model.AnnotationReview> reviews =
        rest.getReviews().stream()
            .map(review -> reviewMapper.toDomain(rest.getId(), review))
            .toList();
    return api.bpartners.annotator.repository.model.AnnotationBatchReview.builder()
        .id(rest.getId())
        .annotationBatchId(rest.getAnnotationBatchId())
        .reviews(reviews)
        .creationDatetime(Instant.now())
        .status(reviewStatusMapper.toDomain(rest.getStatus()))
        .build();
  }
}
