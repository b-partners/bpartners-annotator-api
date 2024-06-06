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
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Task {
  @Id private String id;

  @ManyToOne
  @JoinColumn(name = "job_id", updatable = false)
  @ToString.Exclude
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Task task)) return false;
    return Objects.equals(id, task.id)
        && Objects.equals(filename, task.filename)
        && status == task.status
        && Objects.equals(userId, task.userId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, filename, status, userId);
  }
}
