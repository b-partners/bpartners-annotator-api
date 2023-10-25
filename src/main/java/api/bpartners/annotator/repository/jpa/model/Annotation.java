package api.bpartners.annotator.repository.jpa.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Annotation {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private String id;
    private String taskId;
    @OneToOne
    @JoinColumn(name = "label_id", updatable = false)
    private Label label;
    private String userId;
    @JdbcTypeCode(SqlTypes.JSON)
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
