package api.bpartners.annotator.conf;

import static api.bpartners.annotator.integration.conf.utils.TestMocks.ADMIN_API_KEY;

import org.springframework.test.context.DynamicPropertyRegistry;

public class EnvConf {
  void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("env", () -> "test");
    registry.add("aws.ses.source", () -> "dummy");
    registry.add("aws.cognito.userPool.id", () -> "eu-west-3_vq2jlNjq7");
    registry.add("aws.cognito.userPool.domain", () -> "dummy");
    registry.add("aws.cognito.userPool.clientId", () -> "dummy");
    registry.add("aws.cognito.userPool.clientSecret", () -> "dummy");
    registry.add("admin.api.key", () -> ADMIN_API_KEY);
    registry.add("spring.flyway.locations", () -> "classpath:/db/migration,classpath:/db/testdata");
    registry.add(
        "GEOJOBS.USER.INFO",
        () -> "{\"userId\":\"geo-jobs_user_id\", \"teamId\":\"geo_jobs_team_id\"}");
    registry.add("tasks.insert.limit.max", () -> "5");
  }
}
