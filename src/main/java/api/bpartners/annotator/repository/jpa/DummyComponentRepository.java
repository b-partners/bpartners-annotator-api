package api.bpartners.annotator.repository.jpa;

import api.bpartners.annotator.repository.jpa.model.DummyComponent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DummyComponentRepository extends JpaRepository<DummyComponent, String> {
}
