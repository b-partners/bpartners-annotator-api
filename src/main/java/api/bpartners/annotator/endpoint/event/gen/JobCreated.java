package api.bpartners.annotator.endpoint.event.gen;

import api.bpartners.annotator.repository.jpa.model.Job;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
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
public class JobCreated implements Serializable {
  @JsonProperty("job")
  private Job job;
  @JsonProperty("continuation_token")
  private String continuationToken;
}
