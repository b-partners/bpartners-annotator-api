package api.bpartners.annotator.repository.model;

import static api.bpartners.annotator.repository.model.ExportTaskStatus.HealthStatus.UNKNOWN;
import static api.bpartners.annotator.repository.model.ExportTaskStatus.ProgressionStatus.PENDING;
import static jakarta.persistence.CascadeType.ALL;
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
@Table(name = "\"export_task\"")
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class ExportTask {
  @Id private String id;
  private String jobId;
  @CreationTimestamp private Instant submissionInstant;

  @OneToMany(cascade = ALL, mappedBy = "exportTaskId")
  private List<AnnotationBatch> annotationBatches;

  @OneToMany(cascade = ALL, mappedBy = "taskId")
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
            .getFirst();
  }

  public void hasNewStatus(ExportTaskStatus status) {
    var statusHistory = getStatusHistory();
    if (statusHistory.isEmpty()) {
      statusHistory.add(status);
    } else {
      statusHistory.add(getStatus().to(status));
    }
    this.setStatusHistory(statusHistory);
  }
}
