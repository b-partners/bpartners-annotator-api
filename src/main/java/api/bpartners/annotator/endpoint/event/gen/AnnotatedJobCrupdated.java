package api.bpartners.annotator.endpoint.event.gen;

import api.bpartners.annotator.endpoint.rest.model.CrupdateAnnotatedJob;
import api.bpartners.annotator.repository.model.Job;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class AnnotatedJobCrupdated {
  @JsonProperty("crupdateAnnotatedJob")
  private CrupdateAnnotatedJob crupdateAnnotatedJob;

  @JsonProperty("job")
  private Job job;
}
