package api.bpartners.annotator.repository.model;

import static api.bpartners.annotator.repository.model.enums.TaskStatus.COMPLETED;
import static api.bpartners.annotator.repository.model.enums.TaskStatus.TO_REVIEW;
import static jakarta.persistence.EnumType.STRING;
import static org.hibernate.type.SqlTypes.NAMED_ENUM;

import api.bpartners.annotator.repository.model.enums.TaskStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {
  @Id private String id;

  @ManyToOne
  @JoinColumn(name = "job_id", updatable = false, insertable = true)
  private Job job;

  @Column(name = "filename")
  private String filename;

  @Enumerated(STRING)
  @Column(name = "status")
  @JdbcTypeCode(NAMED_ENUM)
  private TaskStatus status;

  private String userId;

  @JsonIgnore
  public boolean isCompleted() {
    return COMPLETED.equals(this.status);
  }

  @JsonIgnore
  public boolean isToReview() {
    return TO_REVIEW.equals(this.status);
  }
}
