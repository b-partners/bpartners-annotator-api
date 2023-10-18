package api.bpartners.annotator.integration.conf.utils;

import api.bpartners.annotator.integration.conf.DbContextInitializer;
import lombok.Getter;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import static api.bpartners.annotator.integration.conf.utils.TestUtils.findAvailableTcpPort;
import static org.springframework.test.context.support.TestPropertySourceUtils.addInlinedPropertiesToEnvironment;

public class EnvContextInitializer
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  private final DbContextInitializer dbContextInitializer;

  @Getter
  private int httpServerPort = -1;

  public EnvContextInitializer(DbContextInitializer dbContextInitializer) {
    this.dbContextInitializer = dbContextInitializer;
  }


  @Override
  public void initialize(ConfigurableApplicationContext applicationContext) {
    httpServerPort = findAvailableTcpPort();
    var postgresContainer = dbContextInitializer.getPostgresContainer();
    addInlinedPropertiesToEnvironment(
        applicationContext,
        "server.port=" + httpServerPort,
        "spring.datasource.url=" + postgresContainer.getJdbcUrl(),
        "spring.datasource.username=" + postgresContainer.getUsername(),
        "spring.datasource.password=" + postgresContainer.getPassword(),
        "spring.flyway.locations=classpath:/db/migration,"
            + dbContextInitializer.getFlywayTestdataPath());
  }
}
