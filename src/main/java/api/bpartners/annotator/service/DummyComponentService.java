package api.bpartners.annotator.service;

import api.bpartners.annotator.repository.jpa.DummyComponentRepository;
import api.bpartners.annotator.repository.jpa.model.DummyComponent;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DummyComponentService {
  private final DummyComponentRepository repository;

  public DummyComponent getDummyTestData() {
    Optional<DummyComponent> actual = repository.findById("dummy_table_id");
    if(actual.isPresent()) {
      return actual.get();
    }
    return null;
  }
}
