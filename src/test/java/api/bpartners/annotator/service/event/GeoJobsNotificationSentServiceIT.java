package api.bpartners.annotator.service.event;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import api.bpartners.annotator.conf.FacadeIT;
import api.bpartners.annotator.endpoint.event.model.GeoJobsNotificationSent;
import api.bpartners.annotator.service.geojobs.GeoJobsService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class GeoJobsNotificationSentServiceIT extends FacadeIT {
  @Autowired private GeoJobsNotificationSentService subject;
  @MockBean private GeoJobsService geoJobsServiceMock;

  @Test
  void consume_geo_jobs_notification_event() {
    var jobId = randomUUID().toString();
    var event = new GeoJobsNotificationSent(jobId);
    var jobIdCapture = ArgumentCaptor.forClass(String.class);

    subject.accept(event);
    verify(geoJobsServiceMock, times(1)).notify(jobIdCapture.capture());
    var jobIdValue = jobIdCapture.getValue();

    assertEquals(jobId, jobIdValue);
  }
}
