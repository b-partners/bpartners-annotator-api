package api.bpartners.annotator.repository.jpa;

import api.bpartners.annotator.repository.model.AnnotationBatchReview;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AnnotationBatchReviewRepository
    extends JpaRepository<AnnotationBatchReview, String> {
  @Query(
      """
      select abr from AnnotationBatchReview abr
      inner join AnnotationBatch ab on abr.annotationBatchId = ab.id
      inner join Task t on ab.task.id = t.id
      where t.job.id = ?1 and t.id = ?2 and abr.annotationBatchId = ?3
      """)
  List<AnnotationBatchReview> findAllByJobTaskAndAnnotationBatch(
      String jobId, String taskId, String annotationBatchId, Sort sort);

  @Query(
      """
      select abr from AnnotationBatchReview abr
      inner join AnnotationBatch ab on abr.annotationBatchId = ab.id
      inner join Task t on ab.task.id = t.id
      where t.job.id = ?1 and t.id = ?2 and abr.annotationBatchId = ?3
      and abr.id = ?4
      """)
  Optional<AnnotationBatchReview> findByJobTaskAndAnnotationBatchAndId(
      String jobId, String taskId, String annotationBatchId, String reviewId);

  @Query(
      """
      select abr from AnnotationBatchReview abr
      inner join AnnotationBatch ab on abr.annotationBatchId = ab.id
      inner join Task t on ab.task.id = t.id
      where t.userId = ?1 and t.id = ?2 and abr.annotationBatchId = ?3
      """)
  List<AnnotationBatchReview> findAllByUserTaskAndAnnotationBatch(
      String userId, String taskId, String annotationBatchId, Sort sort);

  @Query(
      """
      select abr from AnnotationBatchReview abr
      inner join AnnotationBatch ab on abr.annotationBatchId = ab.id
      inner join Task t on ab.task.id = t.id
      where t.userId = ?1 and t.id = ?2 and abr.annotationBatchId = ?3
      and abr.id = ?4
      """)
  Optional<AnnotationBatchReview> findByUserTaskAndAnnotationBatchAndId(
      String userId, String taskId, String annotationBatchId, String batchReviewId);
}
