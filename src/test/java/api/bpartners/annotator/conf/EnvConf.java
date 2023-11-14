package api.bpartners.annotator.conf;

import org.springframework.test.context.DynamicPropertyRegistry;

public class EnvConf {
  void configureProperties (DynamicPropertyRegistry registry) {
    registry.add("env", () -> "test");
    registry.add("aws.ses.source", () -> "dummy");
  }
}
