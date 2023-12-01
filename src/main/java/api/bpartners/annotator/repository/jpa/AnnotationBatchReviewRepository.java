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
    inner join AnnotationReview ar on ar.annotationBatchReviewId = abr.id
    inner join Annotation a on ar.annotationId is null or ar.annotationId = a.id
    inner join Task t on t.id = a.taskId
    where t.job.id = ?1 and t.id = ?2 and abr.annotationBatchId = ?3
    """)
  List<AnnotationBatchReview> findAllByJobTaskAndAnnotation(
      String jobId, String taskId, String annotationId, Sort sort);

  @Query(
      """
        select abr from AnnotationBatchReview abr
        inner join AnnotationReview ar on ar.annotationBatchReviewId = abr.id
        inner join Annotation a on ar.annotationId is null or ar.annotationId = a.id
        inner join Task t on t.id = a.taskId
        where t.job.id = ?1 and t.id = ?2 and abr.annotationBatchId = ?3
        and abr.id = ?4
        """)
  Optional<AnnotationBatchReview> findByJobTaskAndAnnotationAndId(
      String jobId, String taskId, String annotationId, String reviewId);

  @Query(
      """
        select abr from AnnotationBatchReview abr
        inner join AnnotationReview ar on ar.annotationBatchReviewId = abr.id
        inner join Annotation a on ar.annotationId is null or ar.annotationId = a.id
        inner join Task t on t.id = a.taskId
        where t.userId = ?1 and t.id = ?2 and abr.annotationBatchId = ?3
        """)
  List<AnnotationBatchReview> findAllByUserTaskAndAnnotation(
      String userId, String taskId, String annotationId, Sort sort);

  @Query(
      """
            select abr from AnnotationBatchReview abr
            inner join AnnotationReview ar on ar.annotationBatchReviewId = abr.id
            inner join Annotation a on ar.annotationId is null or ar.annotationId = a.id
            inner join Task t on t.id = a.taskId
            where t.userId = ?1 and t.id = ?2 and abr.annotationBatchId = ?3
            and abr.id = ?4
            """)
  Optional<AnnotationBatchReview> findByUserTaskAndAnnotationAndId(
      String userId, String taskId, String annotationId, String reviewId);
}
