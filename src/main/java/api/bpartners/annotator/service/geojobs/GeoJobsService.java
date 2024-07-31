package api.bpartners.annotator.service.geojobs;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GeoJobsService {
  private final GeoJobsApi geoJobsApi;

  public void notify(String annotationJobId) {
    geoJobsApi.notify(annotationJobId);
  }
}
