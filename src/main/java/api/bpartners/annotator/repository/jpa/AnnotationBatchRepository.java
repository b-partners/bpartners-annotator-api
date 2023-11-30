package api.bpartners.annotator.repository.jpa;

import api.bpartners.annotator.repository.model.AnnotationBatch;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnotationBatchRepository extends JpaRepository<AnnotationBatch, String> {
  List<AnnotationBatch> findAllByTaskId(String taskId, Pageable pageable);

  List<AnnotationBatch> findAllByAnnotatorIdAndTaskId(
      String annotatorId, String taskId, Pageable pageable);

  Optional<AnnotationBatch> findByTaskIdAndId(String taskId, String id);

  Optional<AnnotationBatch> findByAnnotatorIdAndTaskIdAndId(
      String annotatorId, String taskId, String id);
}
