package api.bpartners.annotator.repository.model;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static org.hibernate.type.SqlTypes.NAMED_ENUM;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;

@Entity
@Table(name = "\"job_status\"")
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class ExportTaskStatus implements Serializable {
  @Id
  @GeneratedValue(strategy = IDENTITY)
  private String id;

  private String taskId;

  @Enumerated(STRING)
  @JdbcTypeCode(NAMED_ENUM)
  private ProgressionStatus progression;

  private String message;

  @Enumerated(STRING)
  @JdbcTypeCode(NAMED_ENUM)
  private HealthStatus health;

  @CreationTimestamp private Instant creationDatetime;

  public enum ProgressionStatus {
    PENDING,
    PROCESSING,
    FINISHED;

    private ProgressionStatus to(
        ProgressionStatus newProgression, HealthStatus newHealth, String errorMessage) {
      return switch (this) {
        case PENDING -> newProgression;
        case PROCESSING -> switch (newProgression) {
          case PENDING -> throw new IllegalArgumentException(errorMessage);
          case PROCESSING, FINISHED -> newProgression;
        };
        case FINISHED -> switch (newProgression) {
          case PROCESSING, PENDING -> switch (newHealth) {
            case RETRYING -> newProgression;
            case FAILED, SUCCEEDED, UNKNOWN -> throw new IllegalArgumentException(errorMessage);
          };
          case FINISHED -> newProgression;
        };
      };
    }
  }

  public enum HealthStatus {
    UNKNOWN,
    RETRYING,
    SUCCEEDED,
    FAILED;

    private HealthStatus to(HealthStatus newHealth, String errorMessage) {
      return switch (this) {
        case UNKNOWN -> newHealth;
        case RETRYING -> switch (newHealth) {
          case UNKNOWN, RETRYING -> newHealth;
          case SUCCEEDED, FAILED -> throw new IllegalArgumentException(errorMessage);
        };
        case SUCCEEDED -> switch (newHealth) {
          case SUCCEEDED, RETRYING -> newHealth;
          case UNKNOWN, FAILED -> throw new IllegalArgumentException(errorMessage);
        };
        case FAILED -> switch (newHealth) {
          case FAILED, RETRYING, SUCCEEDED -> newHealth;
          case UNKNOWN -> throw new IllegalArgumentException(errorMessage);
        };
      };
    }
  }
}
