package api.bpartners.annotator.endpoint.event.gen;

import api.bpartners.annotator.repository.model.Job;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
  @JsonIgnoreProperties({"remainingTasksNumber", "tasksCompletedByUserId"})
  private Job job;

  @JsonProperty("next_continuation_token")
  private String nextContinuationToken;
}
