package api.bpartners.annotator.repository.jpa;

import api.bpartners.annotator.repository.model.Annotation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnotationRepository extends JpaRepository<Annotation, String> {
  List<Annotation> findAllByTaskId(String taskId, Pageable pageable);

  Optional<Annotation> findByTaskIdAndId(String taskId, String id);
}
