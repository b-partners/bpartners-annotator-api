package api.bpartners.annotator.conf;

import org.springframework.test.context.DynamicPropertyRegistry;

public class EnvConf {
  void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("env", () -> "test");
    registry.add("aws.ses.source", () -> "dummy");
    registry.add("aws.cognito.userPool.id", () -> "eu-west-3_vq2jlNjq7");
    registry.add("aws.cognito.userPool.domain", () -> "dummy");
    registry.add("aws.cognito.userPool.clientId", () -> "dummy");
    registry.add("aws.cognito.userPool.clientSecret", () -> "dummy");
  }
}
