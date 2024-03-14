package api.bpartners.annotator.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokennedCustomS3ObjectList {
  private String nextContinuationToken;
  private List<String> objectsFilename;
}
