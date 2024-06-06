package api.bpartners.annotator.repository.model;

import static api.bpartners.annotator.repository.model.enums.ReviewStatus.REJECTED;
import static jakarta.persistence.EnumType.STRING;
import static org.hibernate.type.SqlTypes.NAMED_ENUM;

import api.bpartners.annotator.repository.model.enums.ReviewStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnotationBatchReview {
  @Id private String id;

  @Enumerated(STRING)
  @JdbcTypeCode(NAMED_ENUM)
  private ReviewStatus status;

  private String annotationBatchId;

  @CreationTimestamp
  @Column(columnDefinition = "TIMESTAMP WITHOUT TIMEZONE")
  private Instant creationDatetime;

  public Instant getCreationDatetime() {
    return creationDatetime.truncatedTo(ChronoUnit.MILLIS);
  }

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "annotationBatchReviewId")
  @ToString.Exclude
  private List<AnnotationReview> reviews;

  @JsonIgnore
  public boolean isRejected() {
    return REJECTED.equals(this.status);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof AnnotationBatchReview that)) return false;
    return Objects.equals(id, that.id)
        && status == that.status
        && Objects.equals(annotationBatchId, that.annotationBatchId)
        && Objects.equals(creationDatetime, that.creationDatetime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, status, annotationBatchId, creationDatetime);
  }
}
