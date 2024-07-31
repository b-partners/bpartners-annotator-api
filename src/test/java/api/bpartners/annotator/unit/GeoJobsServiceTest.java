package api.bpartners.annotator.unit;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import api.bpartners.annotator.service.geojobs.GeoJobsApi;
import api.bpartners.annotator.service.geojobs.GeoJobsService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class GeoJobsServiceTest {
  private GeoJobsApi geoJobsApiMock = mock();
  private GeoJobsService geoJobsService = new GeoJobsService(geoJobsApiMock);

  @Test
  void notify_geo_jobs() {
    var jobId = randomUUID().toString();
    var jobIdCapture = ArgumentCaptor.forClass(String.class);

    geoJobsService.notify(jobId);
    verify(geoJobsApiMock, times(1)).notify(jobIdCapture.capture());
    var jobIdValue = jobIdCapture.getValue();

    assertEquals(jobId, jobIdValue);
  }
}
