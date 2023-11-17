package api.bpartners.annotator.repository.model;

import static api.bpartners.annotator.repository.model.types.PostgresTypes.JSONB;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Annotation {
  @Id private String id;
  private String taskId;

  @OneToOne
  @JoinColumn(name = "label_id", updatable = false)
  private Label label;

  private String userId;

  @Type(type = JSONB)
  @Column(columnDefinition = JSONB)
  private Polygon polygon;

  @Data
  @Builder
  public static class Polygon {
    private List<Point> points;
  }

  @Data
  @Builder
  public static class Point {
    private double x;
    private double y;
  }
}
