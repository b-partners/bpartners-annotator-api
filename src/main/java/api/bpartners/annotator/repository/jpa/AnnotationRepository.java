package api.bpartners.annotator.repository.jpa;

import api.bpartners.annotator.repository.model.Annotation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

public interface AnnotationRepository extends JpaRepository<Annotation, String> {
  List<Annotation> findByBatchId(@NonNull String batchId);
}
