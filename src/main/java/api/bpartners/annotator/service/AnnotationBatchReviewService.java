package api.bpartners.annotator.service;

import static api.bpartners.annotator.repository.model.enums.ReviewStatus.REJECTED;

import api.bpartners.annotator.model.exception.BadRequestException;
import api.bpartners.annotator.model.exception.NotFoundException;
import api.bpartners.annotator.repository.jpa.AnnotationBatchReviewRepository;
import api.bpartners.annotator.repository.model.AnnotationBatchReview;
import api.bpartners.annotator.repository.model.Task;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AnnotationBatchReviewService {
  private final AnnotationBatchReviewRepository repository;
  private final TaskService taskService;
  private final JobService jobService;

  public List<AnnotationBatchReview> findAllByJobTaskAndAnnotationBatch(
      String jobId, String taskId, String annotationBatchId) {
    return repository.findAllByJobTaskAndAnnotationBatch(
        jobId, taskId, annotationBatchId, Sort.by("creationDatetime"));
  }

  public AnnotationBatchReview findByJobTaskAndAnnotationAndId(
      String jobId, String taskId, String annotationBatchId, String reviewId) {
    return repository
        .findByJobTaskAndAnnotationBatchAndId(jobId, taskId, annotationBatchId, reviewId)
        .orElseThrow(
            () ->
                new NotFoundException(
                    "AnnotationBatchReview identified by id = "
                        + reviewId
                        + " annotation.id = "
                        + annotationBatchId
                        + " task.id = "
                        + taskId
                        + " job.id = "
                        + jobId
                        + " not found"));
  }

  public List<AnnotationBatchReview> findAllByUserTaskAndAnnotation(
      String userId, String taskId, String annotationBatchId) {
    return repository.findAllByUserTaskAndAnnotationBatch(
        userId, taskId, annotationBatchId, Sort.by("creationDatetime"));
  }

  public AnnotationBatchReview findByUserTaskAndAnnotationAndId(
      String userId, String taskId, String annotationBatchId, String reviewId) {
    return repository
        .findByUserTaskAndAnnotationBatchAndId(userId, taskId, annotationBatchId, reviewId)
        .orElseThrow(
            () ->
                new NotFoundException(
                    "AnnotationBatchReview identified by id = "
                        + reviewId
                        + " annotation.id = "
                        + annotationBatchId
                        + " task.id = "
                        + taskId
                        + " user.id = "
                        + userId
                        + " not found"));
  }

  public AnnotationBatchReview save(
      String taskId, String annotationBatchId, AnnotationBatchReview annotationBatchReview) {

    AnnotationBatchReview saved = repository.save(annotationBatchReview);

    if (annotationBatchReview.getStatus() == REJECTED) {
      if (annotationBatchReview.getReviews() == null
          || annotationBatchReview.getReviews().isEmpty()) {
        throw new BadRequestException("Reviews are mandatory for rejected batch review");
      }
      Task task = taskService.reject(taskId);
      jobService.rejectForCorrection(task.getJob().getId());
    }
    return saved;
  }
}
