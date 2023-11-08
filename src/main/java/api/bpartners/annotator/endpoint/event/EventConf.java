package api.bpartners.annotator.endpoint.event;

import api.bpartners.annotator.PojaGenerated;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.sqs.SqsClient;

@PojaGenerated
@Configuration
public class EventConf {
  private final Region region;
  @Getter
  private final String sqsQueue;
  @Getter
  private final String sesSource;

  public EventConf(
      @Value("${aws.region}") Region region, @Value("${aws.sqs.queue.url}") String sqsQueue,
      @Value("${aws.ses.source}") String sesSource) {
    this.region = region;
    this.sqsQueue = sqsQueue;
    this.sesSource = sesSource;
  }

  @Bean
  public SqsClient getSqsClient() {
    return SqsClient.builder().region(region).build();
  }

  @Bean
  public SesClient getSesClient() {
    return SesClient.builder()
        .region(region)
        .build();
  }
}
