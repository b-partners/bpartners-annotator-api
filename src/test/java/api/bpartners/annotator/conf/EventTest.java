package api.bpartners.annotator.conf;

import org.springframework.test.context.DynamicPropertyRegistry;

public class EventTest {

  void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("aws.region", () -> "dummy-region");
    registry.add("aws.sqs.queue.url", () -> "dummy-queue-url");
    registry.add("aws.eventBridge.bus", () -> "dummy-bus-url");
    registry.add("aws.ses.source", () -> "dummy-ses-source");
  }
}
