package api.bpartners.annotator.endpoint.event;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
public class EventConf {
  private final Region region;
  private final String sqsQueue;

  public EventConf(@Value("eu-west-3") Region region,
                   @Value("${sqs.queue.url}") String sqsQueue) {
    this.region = region;
    this.sqsQueue = sqsQueue;
  }

  @Bean
  public SqsClient getSqsClient() {
    return SqsClient.builder()
        .region(region)
        .build();
  }
}
