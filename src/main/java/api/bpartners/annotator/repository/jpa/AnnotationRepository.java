package api.bpartners.annotator.repository.jpa;

import api.bpartners.annotator.repository.model.Annotation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnotationRepository extends JpaRepository<Annotation, String> {}
