package api.bpartners.annotator.repository.model;

import static api.bpartners.annotator.repository.model.enums.TaskStatus.COMPLETED;
import static api.bpartners.annotator.repository.model.enums.TaskStatus.TO_REVIEW;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;

import api.bpartners.annotator.repository.model.enums.TaskStatus;
import api.bpartners.annotator.repository.model.types.PostgresEnumType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
public class Task {
  @Id
  @GeneratedValue(strategy = IDENTITY)
  private String id;

  @ManyToOne
  @JoinColumn(name = "job_id", updatable = false, insertable = true)
  private Job job;

  @Column(name = "filename")
  private String filename;

  @Enumerated(STRING)
  @Column(name = "status")
  @Type(type = "pgsql_enum")
  private TaskStatus status;

  private String userId;

  @JsonIgnore
  public boolean isCompleted() {
    return COMPLETED.equals(this.status);
  }

  @JsonIgnore
  public boolean isToReview() {
    return TO_REVIEW.equals(this.status);
  }
}
