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
              select distinct on (t.id) a.* from annotation_batch a
              inner join task t on t.id = a.task_id
              inner join job j on j.id = t.job_id
              inner join (
                  select a1.task_id, max(a1.creation_timestamp) as latest_creation_timestamp
                  from annotation_batch a1
              inner join task t1 on t1.id = a1.task_id
              inner join job j1 on j1.id = t1.job_id
              where j1.id = :jobId
              group by a1.task_id
              ) max_ct on max_ct.latest_creation_timestamp = a.creation_timestamp
              where j.id = :jobId
          """)
  List<AnnotationBatch> findLatestPerTaskByJobId(@Param("jobId") String jobId);
}
