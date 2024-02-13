package api.bpartners.annotator.repository.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
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

  @ManyToOne
  @JoinColumn(name = "task_id")
  private Task task;

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
