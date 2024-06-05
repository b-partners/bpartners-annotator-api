package api.bpartners.annotator.endpoint.event.model;

import api.bpartners.annotator.endpoint.rest.model.ExportFormat;
import api.bpartners.annotator.repository.model.Job;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.mail.internet.InternetAddress;
import javax.annotation.processing.Generated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Duration;

@Generated("EventBridge")
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Data
@EqualsAndHashCode
@ToString
public class JobExportInitiated extends PojaEvent{
  @JsonProperty("job")
  private Job job;

  @JsonProperty("exportFormat")
  private ExportFormat exportFormat;

  @JsonProperty("emailCC")
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
