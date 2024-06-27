package api.bpartners.annotator.repository.model;

import api.bpartners.annotator.PojaGenerated;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@PojaGenerated
@SuppressWarnings("all")
@Entity
@Getter
@Setter
public class Dummy {
  @Id private String id;
}
