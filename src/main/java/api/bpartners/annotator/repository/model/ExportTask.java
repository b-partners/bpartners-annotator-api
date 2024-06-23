package api.bpartners.annotator.repository.model;

import static api.bpartners.annotator.repository.model.ExportTaskStatus.HealthStatus.SUCCEEDED;
import static api.bpartners.annotator.repository.model.ExportTaskStatus.HealthStatus.UNKNOWN;
import static api.bpartners.annotator.repository.model.ExportTaskStatus.ProgressionStatus.FINISHED;
import static api.bpartners.annotator.repository.model.ExportTaskStatus.ProgressionStatus.PENDING;
import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.EAGER;
import static java.time.Instant.now;
import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "\"export_job\"")
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class ExportTask {
  @Id private String id;
  private String jobId;
  @CreationTimestamp private Instant submissionInstant;

  @OneToMany(cascade = ALL, mappedBy = "taskId")
  private List<AnnotationBatch> annotationBatches;

  @OneToMany(cascade = ALL, mappedBy = "jobId", fetch = EAGER)
  @Builder.Default
  private List<ExportTaskStatus> statusHistory = new ArrayList<>();

  public ExportTaskStatus getStatus() {
    return statusHistory.isEmpty()
        ? ExportTaskStatus.builder()
            .taskId(this.jobId)
            .progression(PENDING)
            .health(UNKNOWN)
            .creationDatetime(now())
            .build()
        : statusHistory.stream()
            .sorted(comparing(ExportTaskStatus::getCreationDatetime, naturalOrder()).reversed())
            .toList()
            .get(0);
  }

  public void setStatusHistory(List<ExportTaskStatus> statusHistory) {
    if (statusHistory == null) {
      this.statusHistory = new ArrayList<>();
      return;
    }
    this.statusHistory = statusHistory;
  }

  public boolean isPending() {
    return PENDING.equals(getStatus().getProgression());
  }

  public boolean isSucceeded() {
    return FINISHED.equals(getStatus().getProgression())
        && SUCCEEDED.equals(getStatus().getHealth());
  }
}
