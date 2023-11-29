package api.bpartners.annotator.repository.model;

import static javax.persistence.EnumType.STRING;

import api.bpartners.annotator.repository.model.enums.ReviewStatus;
import api.bpartners.annotator.repository.model.types.PostgresEnumType;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TypeDef(name = "pgsql_enum", typeClass = PostgresEnumType.class)
public class AnnotationBatchReview {
  @Id private String id;
  private String comment;

  @Type(type = "pgsql_enum")
  @Enumerated(STRING)
  private ReviewStatus status;

  private String annotationId;
  private String annotationBatchId;

  @CreationTimestamp
  @Column(columnDefinition = "TIMESTAMP WITHOUT TIMEZONE")
  private Instant creationDatetime;

  public Instant getCreationDatetime() {
    return creationDatetime.truncatedTo(ChronoUnit.MILLIS);
  }
}
