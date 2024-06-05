package api.bpartners.annotator.endpoint.event.model;

import api.bpartners.annotator.repository.model.Job;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class JobCreated extends PojaEvent {
  @JsonProperty("job")
  private Job job;

  @JsonProperty("next_continuation_token")
  private String nextContinuationToken;

  @Override
  public Duration maxConsumerDuration() {
    return Duration.ofMinutes(10);
  }

  @Override
  public Duration maxConsumerBackoffBetweenRetries() {
    return Duration.ofMinutes(1);
  }
}
