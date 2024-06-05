package api.bpartners.annotator.endpoint.event.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.mail.internet.InternetAddress;
import java.io.Serializable;
import java.time.Duration;
import javax.annotation.processing.Generated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Generated("EventBridge")
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Data
@EqualsAndHashCode
@ToString
public class AnnotationStatisticsComputationTriggered extends PojaEvent {
  @JsonProperty("job_id")
  private String jobId;

  @JsonProperty("email_cc")
  private InternetAddress emailCC;

  @Override
  public Duration maxConsumerDuration() {
    return Duration.ofMinutes(2);
  }

  @Override
  public Duration maxConsumerBackoffBetweenRetries() {
    return Duration.ofMinutes(1);
  }
}
