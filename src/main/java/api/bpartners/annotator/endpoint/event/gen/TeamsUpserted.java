package api.bpartners.annotator.endpoint.event.gen;

import api.bpartners.annotator.endpoint.rest.model.Team;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
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
public class TeamsUpserted {
  @JsonProperty("teams_to_create")
  private List<Team> teamsToCreate;
}
