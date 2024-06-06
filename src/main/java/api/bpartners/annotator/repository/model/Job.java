package api.bpartners.annotator.repository.model;

import static api.bpartners.annotator.repository.model.enums.JobStatus.COMPLETED;
import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.EnumType.STRING;
import static org.hibernate.type.SqlTypes.NAMED_ENUM;

import api.bpartners.annotator.endpoint.rest.model.JobType;
import api.bpartners.annotator.endpoint.rest.model.TaskStatistics;
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
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("status != 'FAILED'::job_status")
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
  @JdbcTypeCode(NAMED_ENUM)
  private JobType type;

  @OneToMany
  @JoinColumn(insertable = false, updatable = false, name = "job_id", referencedColumnName = "id")
  @JsonIgnoreProperties("job")
  @ToString.Exclude
  private List<Task> tasks;

  private int imagesHeight;
  private int imagesWidth;

  @ManyToMany(cascade = ALL)
  @JoinTable(
      name = "has_label",
      joinColumns = @JoinColumn(name = "job_id"),
      inverseJoinColumns = @JoinColumn(name = "label_id"))
  @ToString.Exclude
  private List<Label> labels;

  public String getFolderPath() {
    if (folderPath == null) {
      return "";
    }
    return folderPath;
  }

  @JsonIgnore
  public boolean isCompleted() {
    return COMPLETED.equals(this.status);
  }

  @JsonIgnore
  public TaskStatistics getTaskStatistics(String userId, List<String> externalAnnotatorUserId) {
    assert (userId != null) : "UserId value missing.";
    assert (externalAnnotatorUserId != null) : "externalAnnotatorUserId value missing.";
    List<Task> thisTasks = getTasks();
    long size = thisTasks.size();
    AtomicLong tasksCompletedByUserId = new AtomicLong(0);
    AtomicLong remainingTasksNumber = new AtomicLong(0);
    AtomicLong remainingTasksForUserId = new AtomicLong(0);

    thisTasks.forEach(
        task -> {
          boolean isTaskCompleted = task.isCompleted();
          if (isTaskCompleted) {
            if (userId.equals(task.getUserId())) {
              tasksCompletedByUserId.incrementAndGet();
            }
          } else {
            if (!task.isToReview()) {
              remainingTasksNumber.incrementAndGet();
              String jobUserId = task.getUserId();
              if (jobUserId == null
                  || userId.equals(jobUserId)
                  || externalAnnotatorUserId.contains(jobUserId)) {
                remainingTasksForUserId.incrementAndGet();
              }
            }
          }
        });

    return new TaskStatistics()
        .completedTasksByUserId(tasksCompletedByUserId.longValue())
        .totalTasks(size)
        .remainingTasks(remainingTasksNumber.longValue())
        .remainingTasksForUserId(remainingTasksForUserId.longValue());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Job job)) return false;
    return imagesHeight == job.imagesHeight
        && imagesWidth == job.imagesWidth
        && Objects.equals(id, job.id)
        && Objects.equals(name, job.name)
        && Objects.equals(bucketName, job.bucketName)
        && Objects.equals(folderPath, job.folderPath)
        && Objects.equals(ownerEmail, job.ownerEmail)
        && status == job.status
        && Objects.equals(teamId, job.teamId)
        && type == job.type;
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        id,
        name,
        bucketName,
        folderPath,
        ownerEmail,
        status,
        teamId,
        type,
        imagesHeight,
        imagesWidth);
  }
}
