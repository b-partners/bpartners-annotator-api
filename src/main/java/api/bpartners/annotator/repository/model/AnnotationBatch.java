package api.bpartners.annotator.repository.model;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnotationBatch {
  @Id private String id;
  private String taskId;
  private String annotatorId;

  @OneToMany(mappedBy = "batchId", cascade = CascadeType.ALL)
  private List<Annotation> annotations;

  public List<Annotation> getAnnotations() {
    if (annotations == null) {
      return List.of();
    }
    return annotations;
  }

  @CreationTimestamp private Instant creationTimestamp;

  public Instant getCreationTimestamp() {
    return creationTimestamp.truncatedTo(ChronoUnit.MILLIS);
  }
}
