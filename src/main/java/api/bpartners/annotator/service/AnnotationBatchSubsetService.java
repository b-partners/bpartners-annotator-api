package api.bpartners.annotator.service;

import api.bpartners.annotator.model.exception.NotFoundException;
import api.bpartners.annotator.repository.jpa.AnnotationBatchSubsetRepository;
import api.bpartners.annotator.repository.model.AnnotationBatchSubset;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AnnotationBatchSubsetService {
  private final AnnotationBatchSubsetRepository repository;

  public AnnotationBatchSubset getSubSetById(String id) {
    return repository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Subset.id=" + id + " is not found."));
  }

  public List<AnnotationBatchSubset> saveAll(List<AnnotationBatchSubset> subsets) {
    return repository.saveAll(subsets);
  }
}
