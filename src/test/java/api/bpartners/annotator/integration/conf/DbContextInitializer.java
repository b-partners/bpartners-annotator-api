package api.bpartners.annotator.integration.conf;

import java.util.List;
import lombok.Getter;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import static api.bpartners.annotator.integration.conf.utils.TestUtils.findAvailablePort;

public class DbContextInitializer
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  @Getter
  private final String flywayTestdataPath = "classpath:/db/testdata";
  private JdbcDatabaseContainer<?> postgresContainer;

  @Override
  public void initialize(ConfigurableApplicationContext applicationContext) {
    int localPort = findAvailablePort();
    int containerPort = 5432;

    postgresContainer = new PostgreSQLContainer<>()
        .withDatabaseName("it-db")
        .withUsername("sa")
        .withPassword("sa")
        .withExposedPorts(containerPort);
    postgresContainer.setPortBindings(List.of(String.format("%d:%d", containerPort, localPort)));

    postgresContainer.start();
  }

  public JdbcDatabaseContainer<?> getPostgresContainer() {
    return postgresContainer;
  }
}
