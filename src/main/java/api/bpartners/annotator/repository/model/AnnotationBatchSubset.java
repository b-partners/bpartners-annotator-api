package api.bpartners.annotator.repository.model;

import static org.hibernate.type.SqlTypes.JSON;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;

@Entity
@Table(name = "\"annotation_batch_subset\"")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnotationBatchSubset {
  @Id private String id;
  private String jobId;

  @JdbcTypeCode(JSON)
  @Column(name = "batches")
  private List<AnnotationBatch> batches;
}
