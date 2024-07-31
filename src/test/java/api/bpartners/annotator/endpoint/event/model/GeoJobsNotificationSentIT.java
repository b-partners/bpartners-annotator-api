package api.bpartners.annotator.endpoint.event.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import api.bpartners.annotator.conf.FacadeIT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class GeoJobsNotificationSentIT extends FacadeIT {

  @Autowired ObjectMapper om;

  @Test
  public void serialize_then_deserialize() throws JsonProcessingException {
    var geoJobsNotificationSent = new GeoJobsNotificationSent("dummy");

    var serialized = om.writeValueAsString(geoJobsNotificationSent);
    var deserialized = om.readValue(serialized, GeoJobsNotificationSent.class);

    assertEquals(geoJobsNotificationSent, deserialized);
    assertEquals("dummy", deserialized.getAnnotationJobId());
    assertEquals(Duration.ofMinutes(1), deserialized.maxConsumerBackoffBetweenRetries());
    assertEquals(Duration.ofMinutes(10), deserialized.maxConsumerDuration());
  }
}
