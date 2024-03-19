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
import org.hibernate.annotations.JdbcTypeCode;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Task {
  @Id private String id;

  @ManyToOne
  @JoinColumn(name = "job_id", updatable = false)
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
  public String toString() {
    return "Task{"
        + "id='"
        + id
        + '\''
        +
        // ignore job for LazyInitialization
        ", filename='"
        + filename
        + '\''
        + ", status="
        + status
        + ", userId='"
        + userId
        + '\''
        + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Task task = (Task) o;

    if (!id.equals(task.id)) return false;
    if (!filename.equals(task.filename)) return false;
    if (status != task.status) return false;
    return Objects.equals(userId, task.userId);
  }

  @Override
  public int hashCode() {
    int result = id.hashCode();
    result = 31 * result + filename.hashCode();
    result = 31 * result + status.hashCode();
    result = 31 * result + (userId != null ? userId.hashCode() : 0);
    return result;
  }
}
