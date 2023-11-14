package api.bpartners.annotator.repository.model;

import api.bpartners.annotator.repository.model.enums.TaskStatus;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnTransformer;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;

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
  @Column(name = "filename")
  private String filename;
  @Enumerated(STRING)
  @Column(name = "status")
  @ColumnTransformer(read = "CAST(status AS varchar)", write = "CAST(? AS task_status)")
  private TaskStatus status;
  private String userId;
}
