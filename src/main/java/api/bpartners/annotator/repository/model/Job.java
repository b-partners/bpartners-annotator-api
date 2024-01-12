package api.bpartners.annotator.repository.model;

import static api.bpartners.annotator.repository.model.enums.TaskStatus.COMPLETED;
import static api.bpartners.annotator.repository.model.enums.TaskStatus.TO_REVIEW;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.EnumType.STRING;

import api.bpartners.annotator.repository.model.enums.JobStatus;
import api.bpartners.annotator.repository.model.enums.TaskStatus;
import api.bpartners.annotator.repository.model.types.PostgresEnumType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TypeDef(name = "pgsql_enum", typeClass = PostgresEnumType.class)
public class Job {
  @Id private String id;
  private String name;
  private String bucketName;
  private String folderPath;
  private String ownerEmail;

  @Enumerated(STRING)
  @Column(name = "status")
  @Type(type = "pgsql_enum")
  private JobStatus status;

  private String teamId;

  @OneToMany()
  @JoinColumn(insertable = false, updatable = false, name = "job_id", referencedColumnName = "id")
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

  public long getRemainingTasksNumber() {
    return getTasks().stream()
        .filter(
            task -> {
              TaskStatus status = task.getStatus();
              return !COMPLETED.equals(status) && !TO_REVIEW.equals(status);
            })
        .count();
  }

  @JsonIgnore
  public long getRemainingTasksForUserId(String userId) {
    assert (userId != null) : "UserId value missing.";
    return getTasks().stream()
        .filter(
            task -> {
              TaskStatus status = task.getStatus();
              return (!COMPLETED.equals(status) && !TO_REVIEW.equals(status))
                  && (task.getUserId() == null || userId.equals(task.getUserId()));
            })
        .count();
  }

  public long getTasksCompletedByUserId(String userId) {
    assert (userId != null) : "UserId value missing.";
    return getTasks().stream()
        .filter(task -> userId.equals(task.getUserId()) && COMPLETED.equals(task.getStatus()))
        .count();
  }
}
