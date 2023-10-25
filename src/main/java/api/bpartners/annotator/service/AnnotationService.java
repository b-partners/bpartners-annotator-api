package api.bpartners.annotator.service;

import api.bpartners.annotator.repository.jpa.AnnotationRepository;
import api.bpartners.annotator.repository.jpa.model.Annotation;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@AllArgsConstructor
public class AnnotationService {
  private final AnnotationRepository repository;

  public List<Annotation> save(List<Annotation> annotations) {
    return repository.saveAll(annotations);
  }
}
