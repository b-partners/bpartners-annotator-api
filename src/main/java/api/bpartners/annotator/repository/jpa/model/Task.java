package api.bpartners.annotator.repository.jpa.model;

import api.bpartners.annotator.repository.jpa.model.enums.TaskStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnTransformer;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {
  @Id
  @GeneratedValue(strategy = IDENTITY)
  private String id;
  @ManyToOne
  @JoinColumn(name = "job_id", updatable = false, insertable = true)
  private Job job;
  @Column(name = "s3_image_key")
  private String s3ImageKey;
  @Enumerated(STRING)
  @Column(name = "status")
  @ColumnTransformer(read = "CAST(status AS varchar)", write = "CAST(? AS task_status)")
  private TaskStatus status;
  private String userId;
}
