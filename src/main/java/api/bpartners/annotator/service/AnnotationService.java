package api.bpartners.annotator.service;

import api.bpartners.annotator.repository.jpa.AnnotationRepository;
import api.bpartners.annotator.repository.model.Annotation;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AnnotationService {
  private final AnnotationRepository repository;

  public List<Annotation> save(List<Annotation> annotations) {
    return repository.saveAll(annotations);
  }
}
