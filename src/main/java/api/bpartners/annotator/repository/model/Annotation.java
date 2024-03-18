package api.bpartners.annotator.repository.model;

import static org.hibernate.type.SqlTypes.JSON;

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

  @JdbcTypeCode(JSON)
  private Polygon polygon;

  private String batchId;

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Polygon implements Serializable {
    private List<Point> points;

    public List<Double> getBoundingBox() {
      double minX = Double.MAX_VALUE;
      double minY = Double.MAX_VALUE;
      double maxX = Double.MIN_VALUE;
      double maxY = Double.MIN_VALUE;

      for (Annotation.Point point : this.getPoints()) {
        double x = point.getX();
        double y = point.getY();

        minX = Math.min(minX, x);
        minY = Math.min(minY, y);
        maxX = Math.max(maxX, x);
        maxY = Math.max(maxY, y);
      }

      // Bounding box format: [x_min, y_min, width, height]
      return List.of(minX, minY, maxX - minX, maxY - minY);
    }

    public double getArea() {
      // Assuming the segmentation defines a polygon, you can use the shoelace formula
      double area = 0.0;
      List<Annotation.Point> points = this.getPoints();
      int n = points.size();

      for (int i = 0; i < n; i++) {
        double xi = points.get(i).getX();
        double yi = points.get(i).getY();
        double xj = points.get((i + 1) % n).getX();
        double yj = points.get((i + 1) % n).getY();

        area += xi * yj - xj * yi;
      }

      area = Math.abs(area) / 2.0;
      return area;
    }
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
