package api.bpartners.annotator.repository.jpa;

import api.bpartners.annotator.repository.model.AnnotationBatch;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AnnotationBatchRepository extends JpaRepository<AnnotationBatch, String> {
  List<AnnotationBatch> findAllByTaskId(String taskId, Pageable pageable);

  List<AnnotationBatch> findAllByAnnotatorIdInAndTaskId(
      Collection<String> annotatorId, String taskId, Pageable pageable);

  Optional<AnnotationBatch> findByTaskIdAndId(String taskId, String id);

  Optional<AnnotationBatch> findByAnnotatorIdInAndTaskIdAndId(
      Collection<String> annotatorId, String taskId, String id);

  @Query(
      nativeQuery = true,
      value =
          """
					 SELECT distinct on (t.id) ab.id, ab.task_id, ab.annotator_id, ab.creation_timestamp, ab.export_task_id FROM annotation_batch_review abr
					 inner join public.annotation_batch ab on abr.annotation_batch_id = ab.id
					 inner join public.task t on ab.task_id = t.id
					 inner join public.job j on j.id = t.job_id
					 where abr.status = cast('ACCEPTED' as review_status) and j.id = :jobId
					 group by t.id, ab.id, ab.task_id, ab.annotator_id, ab.creation_timestamp, ab.export_task_id;
				""")
  List<AnnotationBatch> findLatestPerTaskByJobId(@Param("jobId") String jobId);
}
