package api.bpartners.annotator.service;

import api.bpartners.annotator.model.exception.NotFoundException;
import api.bpartners.annotator.repository.jpa.AnnotationBatchReviewRepository;
import api.bpartners.annotator.repository.model.AnnotationBatchReview;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AnnotationBatchReviewService {
  private final AnnotationBatchReviewRepository repository;

  public List<AnnotationBatchReview> findAllByJobTaskAndAnnotation(
      String jobId, String taskId, String annotationId) {
    return repository.findAllByJobTaskAndAnnotation(
        jobId, taskId, annotationId, Sort.by("creationDatetime"));
  }

  public AnnotationBatchReview findByJobTaskAndAnnotationAndId(
      String jobId, String taskId, String annotationId, String reviewId) {
    return repository
        .findByJobTaskAndAnnotationAndId(jobId, taskId, annotationId, reviewId)
        .orElseThrow(
            () ->
                new NotFoundException(
                    "AnnotationBatchReview identified by id = "
                        + reviewId
                        + " annotation.id = "
                        + annotationId
                        + " task.id = "
                        + taskId
                        + " job.id = "
                        + jobId
                        + " not found"));
  }

  public List<AnnotationBatchReview> saveAll(
      String taskId, String annotationId, List<AnnotationBatchReview> annotationBatchReviews) {
    return repository.saveAll(annotationBatchReviews);
  }
}
