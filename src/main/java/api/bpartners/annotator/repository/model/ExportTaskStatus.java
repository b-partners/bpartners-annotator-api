package api.bpartners.annotator.repository.model;

import api.bpartners.annotator.model.Status;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "\"export_task_status\"")
@AllArgsConstructor
@SuperBuilder
@NoArgsConstructor
public class ExportTaskStatus extends Status {
  private String taskId;
}
