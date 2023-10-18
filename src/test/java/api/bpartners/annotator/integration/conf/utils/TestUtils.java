package api.bpartners.annotator.integration.conf.utils;

import org.springframework.test.util.TestSocketUtils;

public class TestUtils {

  public static int findAvailablePort() {
    return TestSocketUtils.findAvailableTcpPort();
  }

  public static int findAvailableTcpPort() {
    return TestSocketUtils.findAvailableTcpPort();
  }
}
