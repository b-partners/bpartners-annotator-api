package api.bpartners.annotator.endpoint.event.model;

import api.bpartners.annotator.repository.model.Team;
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
public class TeamUpserted extends PojaEvent {
  @JsonProperty("team")
  private Team team;

  @Override
  public Duration maxConsumerDuration() {
    return Duration.ofMinutes(3);
  }

  @Override
  public Duration maxConsumerBackoffBetweenRetries() {
    return Duration.ofMinutes(1);
  }
}
