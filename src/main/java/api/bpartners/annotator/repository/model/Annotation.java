package api.bpartners.annotator.repository.model;

import static api.bpartners.annotator.repository.model.types.PostgresTypes.JSONB;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import java.io.Serializable;
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
import org.hibernate.annotations.TypeDef;
import org.springframework.lang.Nullable;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TypeDef(name = JSONB, typeClass = JsonBinaryType.class)
public class Annotation implements Serializable {
  @Id private String id;
  private String taskId;

  @OneToOne
  @JoinColumn(name = "label_id", updatable = false)
  @Nullable
  private Label label;

  private String userId;

  @Type(type = JSONB)
  @Column(columnDefinition = JSONB)
  private Polygon polygon;

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
