package api.bpartners.annotator.repository.model;

import static jakarta.persistence.EnumType.STRING;
import static org.hibernate.type.SqlTypes.NAMED_ENUM;

import api.bpartners.annotator.endpoint.rest.model.ExportFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnotationBatchPageExport implements Statusable<AnnotationBatchPageExportStatus> {
  @Id private String id;
  private String jobId;
  private int beginPage;
  private int pageSize;
  private String bucketKey;

  @Enumerated(STRING)
  @JdbcTypeCode(NAMED_ENUM)
  private ExportFormat exportFormat;

  @OneToMany(mappedBy = "jobId")
  private List<AnnotationBatchPageExportStatus> statuses;

  @Override
  public List<AnnotationBatchPageExportStatus> getStatusHistory() {
    return statuses == null ? List.of() : statuses;
  }

  @Override
  public void setStatusHistory(List<AnnotationBatchPageExportStatus> statusHistory) {
    if (statusHistory == null) {
      this.statuses = new ArrayList<>();
    }
    this.statuses = statusHistory;
  }

  @Override
  public AnnotationBatchPageExportStatus from(Status status) {
    return AnnotationBatchPageExportStatus.from(id, status);
  }
}
