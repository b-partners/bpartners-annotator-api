package api.bpartners.annotator.repository.model;

import static api.bpartners.annotator.repository.model.enums.JobStatus.COMPLETED;
import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.EnumType.STRING;
import static org.hibernate.type.SqlTypes.NAMED_ENUM;

import api.bpartners.annotator.endpoint.rest.model.JobType;
import api.bpartners.annotator.repository.model.enums.JobStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import java.util.List;
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
public class Job {

  @Id private String id;
  private String name;
  private String bucketName;
  private String folderPath;
  private String ownerEmail;

  @Enumerated(STRING)
  @Column(name = "status")
  @JdbcTypeCode(NAMED_ENUM)
  private JobStatus status;

  private String teamId;

  @Enumerated(STRING)
  @Column(name = "type")
  private JobType type;

  @OneToMany
  @JoinColumn(insertable = false, updatable = false, name = "job_id", referencedColumnName = "id")
  @JsonIgnoreProperties("job")
  private List<Task> tasks;

  @ManyToMany(cascade = ALL)
  @JoinTable(
      name = "has_label",
      joinColumns = @JoinColumn(name = "job_id"),
      inverseJoinColumns = @JoinColumn(name = "label_id"))
  private List<Label> labels;

  public String getFolderPath() {
    if (folderPath == null) {
      return "";
    }
    return folderPath;
  }

  @JsonIgnore
  public long getRemainingTasksNumber() {
    return getTasks().stream().filter(task -> !task.isCompleted() && !task.isToReview()).count();
  }

  @JsonIgnore
  public long getRemainingTasksForUserId(String userId) {
    assert (userId != null) : "UserId value missing.";
    return getTasks().stream()
        .filter(
            task ->
                (!task.isCompleted() && !task.isToReview())
                    && (task.getUserId() == null || userId.equals(task.getUserId())))
        .count();
  }

  @JsonIgnore
  public long getTasksCompletedByUserId(String userId) {
    assert (userId != null) : "UserId value missing.";
    return getTasks().stream()
        .filter(task -> userId.equals(task.getUserId()) && task.isCompleted())
        .count();
  }

  @JsonIgnore
  public boolean isCompleted() {
    return COMPLETED.equals(this.status);
  }
}
