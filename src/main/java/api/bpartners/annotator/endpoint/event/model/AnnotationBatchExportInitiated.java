package api.bpartners.annotator.endpoint.event.model;

import api.bpartners.annotator.endpoint.rest.model.ExportFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class AnnotationBatchExportInitiated extends PojaEvent {
  @JsonProperty("id")
  private String id;

  @JsonProperty("job_id")
  private String jobId;

  @JsonProperty("job_export_id")
  private String jobExportId;

  @JsonProperty("exportFormat")
  private ExportFormat exportFormat;

  @JsonProperty("begin_page")
  private int beginPage;

  @JsonProperty("page_size")
  private int pageSize;

  @Override
  public Duration maxConsumerDuration() {
    return Duration.ofMinutes(1);
  }

  @Override
  public Duration maxConsumerBackoffBetweenRetries() {
    return Duration.ofMinutes(1);
  }
}
