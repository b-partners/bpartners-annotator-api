package api.bpartners.annotator.service.aws;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;

@Configuration
@Getter
public class SesConf {
  private final Region region;
  private final String sesSource;

  public SesConf(
      @Value("${aws.region}") Region region, @Value("${aws.ses.source}") String sesSource) {
    this.region = region;
    this.sesSource = sesSource;
  }

  @Bean
  public SesClient getSesClient() {
    return SesClient.builder().region(region).build();
  }
}
