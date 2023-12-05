package api.bpartners.annotator.service;

import static api.bpartners.annotator.repository.model.enums.ReviewStatus.REJECTED;
import static api.bpartners.annotator.repository.model.enums.TaskStatus.TO_CORRECT;

import api.bpartners.annotator.model.exception.BadRequestException;
import api.bpartners.annotator.model.exception.NotFoundException;
import api.bpartners.annotator.repository.jpa.AnnotationBatchReviewRepository;
import api.bpartners.annotator.repository.model.AnnotationBatchReview;
import api.bpartners.annotator.repository.model.Task;
import api.bpartners.annotator.repository.model.enums.JobStatus;
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

  public List<AnnotationBatchReview> findAllByUserTaskAndAnnotation(
      String userId, String taskId, String annotationId) {
    return repository.findAllByUserTaskAndAnnotation(
        userId, taskId, annotationId, Sort.by("creationDatetime"));
  }

  public AnnotationBatchReview findByUserTaskAndAnnotationAndId(
      String userId, String taskId, String annotationId, String reviewId) {
    return repository
        .findByUserTaskAndAnnotationAndId(userId, taskId, annotationId, reviewId)
        .orElseThrow(
            () ->
                new NotFoundException(
                    "AnnotationBatchReview identified by id = "
                        + reviewId
                        + " annotation.id = "
                        + annotationId
                        + " task.id = "
                        + taskId
                        + " user.id = "
                        + userId
                        + " not found"));
  }

  public AnnotationBatchReview save(
      String taskId, String annotationId, AnnotationBatchReview annotationBatchReview) {

    AnnotationBatchReview saved = repository.save(annotationBatchReview);

    if (annotationBatchReview.getStatus() == REJECTED) {
      if (annotationBatchReview.getReviews() == null
          || annotationBatchReview.getReviews().isEmpty()) {
        throw new BadRequestException("Reviews are mandatory for rejected batch review");
      }
      Task task = taskService.updateStatus(taskId, TO_CORRECT);
      jobService.updateJobStatus(task.getJob().getId(), JobStatus.TO_CORRECT);
    }
    return saved;
  }
}
