package api.bpartners.annotator.repository.jpa;

import api.bpartners.annotator.repository.jpa.model.Label;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabelRepository extends JpaRepository<Label, String> {
}
