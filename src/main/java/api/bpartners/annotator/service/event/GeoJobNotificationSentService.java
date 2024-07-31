package api.bpartners.annotator.service.event;

import api.bpartners.annotator.endpoint.event.model.GeoJobNotificationSent;
import api.bpartners.annotator.service.geojobs.GeoJobsService;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GeoJobNotificationSentService implements Consumer<GeoJobNotificationSent> {
  private final GeoJobsService geoJobsService;

  @Override
  public void accept(GeoJobNotificationSent geoJobNotificationSent) {
    var jobId = geoJobNotificationSent.getAnnotationJobId();
    geoJobsService.notify(jobId);
  }
}
