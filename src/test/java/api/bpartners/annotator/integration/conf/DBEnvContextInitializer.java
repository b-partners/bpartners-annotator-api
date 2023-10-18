package api.bpartners.annotator.integration.conf;

import api.bpartners.annotator.integration.conf.utils.EnvContextInitializer;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class DBEnvContextInitializer
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  private static int httpServerPort;
  private final DbContextInitializer dbContextInitializer = new DbContextInitializer();
  private final EnvContextInitializer envContextInitializer =
      new EnvContextInitializer(dbContextInitializer);

  public static int getHttpServerPort() {
    return httpServerPort;
  }

  @Override
  public void initialize(ConfigurableApplicationContext applicationContext) {
    dbContextInitializer.initialize(applicationContext);
    envContextInitializer.initialize(applicationContext);
    httpServerPort = envContextInitializer.getHttpServerPort();
  }
}
