package api.bpartners.annotator.repository.model;

import static org.postgresql.core.Oid.JSONB;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.springframework.lang.Nullable;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Annotation implements Serializable {
  @Id private String id;
  private String taskId;

  @OneToOne
  @JoinColumn(name = "label_id", updatable = false)
  @Nullable
  private Label label;

  private String userId;

  @JdbcTypeCode(JSONB)
  private Polygon polygon;

  private String batchId;

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Polygon implements Serializable {
    private List<Point> points;
  }

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Point implements Serializable {
    private double x;
    private double y;
  }
}
