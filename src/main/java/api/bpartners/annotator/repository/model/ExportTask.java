package api.bpartners.annotator.repository.model;

import static jakarta.persistence.CascadeType.ALL;

import api.bpartners.annotator.model.Status;
import api.bpartners.annotator.model.Statusable;
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
public class ExportTask implements Statusable<ExportTaskStatus> {
  @Id private String id;
  private String jobId;
  @CreationTimestamp private Instant submissionInstant;

  @OneToMany(cascade = ALL, mappedBy = "exportTaskId")
  private List<AnnotationBatch> annotationBatches;

  @OneToMany(cascade = ALL, mappedBy = "taskId")
  @Builder.Default
  private List<ExportTaskStatus> statusHistory = new ArrayList<>();

  @Override
  public ExportTaskStatus from(Status status) {
    return ExportTaskStatus.builder()
        .id(status.getId())
        .taskId(id)
        .health(status.getHealth())
        .progression(status.getProgression())
        .creationDatetime(status.getCreationDatetime())
        .build();
  }
}
