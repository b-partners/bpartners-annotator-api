package api.bpartners.annotator.repository.model;

import static jakarta.persistence.EnumType.STRING;
import static org.hibernate.type.SqlTypes.NAMED_ENUM;

import api.bpartners.annotator.endpoint.rest.model.ExportFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
public class JobExport implements Statusable<JobExportStatus> {
  @Id private String id;
  private String jobId;
  private String emailOwner;
  private String emailCC;

  @Enumerated(STRING)
  @JdbcTypeCode(NAMED_ENUM)
  private ExportFormat exportFormat;

  @OneToMany(mappedBy = "jobId")
  private List<JobExportStatus> statuses;

  @Override
  public List<JobExportStatus> getStatusHistory() {
    return statuses == null ? List.of() : statuses;
  }

  @Override
  public void setStatusHistory(List<JobExportStatus> statusHistory) {}

  @Override
  public JobExportStatus from(Status status) {
    return JobExportStatus.from(id, status);
  }
}
