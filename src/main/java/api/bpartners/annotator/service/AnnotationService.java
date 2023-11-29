package api.bpartners.annotator.service;

import api.bpartners.annotator.model.BoundedPageSize;
import api.bpartners.annotator.model.PageFromOne;
import api.bpartners.annotator.model.exception.NotFoundException;
import api.bpartners.annotator.repository.jpa.AnnotationRepository;
import api.bpartners.annotator.repository.model.Annotation;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AnnotationService {
  private final AnnotationRepository repository;

  public List<Annotation> save(List<Annotation> annotations) {
    return repository.saveAll(annotations);
  }

  public List<Annotation> findAllByTask(String taskId, PageFromOne page, BoundedPageSize pageSize) {
    Pageable pageable = PageRequest.of(page.getValue() - 1, pageSize.getValue());
    return repository.findAllByTaskId(taskId, pageable);
  }

  public Annotation findByTaskIdAndId(String taskId, String id) {
    return repository
        .findByTaskIdAndId(taskId, id)
        .orElseThrow(
            () ->
                new NotFoundException(
                    "Annotation identified by id = "
                        + id
                        + " and taskId = "
                        + taskId
                        + " not found"));
  }
}
