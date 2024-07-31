package api.bpartners.annotator.service.event;

import api.bpartners.annotator.endpoint.event.model.GeoJobsNotificationSent;
import api.bpartners.annotator.service.geojobs.GeoJobsService;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GeoJobsNotificationSentService implements Consumer<GeoJobsNotificationSent> {
  private final GeoJobsService geoJobsService;

  @Override
  public void accept(GeoJobsNotificationSent geoJobsNotificationSent) {
    var jobId = geoJobsNotificationSent.getAnnotationJobId();
    geoJobsService.notify(jobId);
  }
}
