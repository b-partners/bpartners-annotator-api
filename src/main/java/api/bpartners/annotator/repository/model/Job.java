package api.bpartners.annotator.repository.model;

import api.bpartners.annotator.repository.model.enums.JobStatus;
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
import org.hibernate.annotations.ColumnTransformer;

import static javax.persistence.EnumType.STRING;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Job {
  @Id
  private String id;
  private String name;
  private String bucketName;
  private String folderPath;
  private String ownerEmail;
  @Enumerated(STRING)
  @Column(name = "status")
  @ColumnTransformer(read = "CAST(status AS varchar)", write = "CAST(? AS job_status)")
  private JobStatus status;
  private String teamId;
  @OneToMany()
  @JoinColumn(insertable = false, updatable = false, name = "job_id", referencedColumnName = "id")
  private List<Task> tasks;
  @ManyToMany()
  @JoinTable(name = "has_label", joinColumns = @JoinColumn(name = "job_id"), inverseJoinColumns = @JoinColumn(name = "label_id"))
  private List<Label> labels;

  public String getFolderPath() {
    if (folderPath == null) {
      return "";
    }
    return folderPath;
  }
}
