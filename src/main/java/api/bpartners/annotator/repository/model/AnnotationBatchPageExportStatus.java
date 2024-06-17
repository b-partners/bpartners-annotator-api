package api.bpartners.annotator.repository.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@PrimaryKeyJoinColumn(name = "id")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Table(name = "annotation_batch_export_status")
public class AnnotationBatchPageExportStatus extends Status {
  @JoinColumn(referencedColumnName = "id")
  private String jobId;

  public static AnnotationBatchPageExportStatus from(String id, Status status) {
    return AnnotationBatchPageExportStatus.builder()
        .jobId(id)
        .id(status.getId())
        .progression(status.getProgression())
        .health(status.getHealth())
        .message(status.getMessage())
        .creationDatetime(status.getCreationDatetime())
        .build();
  }
}
