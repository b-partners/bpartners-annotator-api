package api.bpartners.annotator.integration;

import api.bpartners.annotator.endpoint.rest.api.SecurityApi;
import api.bpartners.annotator.endpoint.rest.client.ApiClient;
import api.bpartners.annotator.endpoint.rest.client.ApiException;
import api.bpartners.annotator.endpoint.rest.model.DummyComponent;
import api.bpartners.annotator.integration.conf.AbstractContextInitializer;
import api.bpartners.annotator.integration.conf.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;

import static api.bpartners.annotator.integration.conf.utils.TestUtils.anAvailableRandomPort;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Testcontainers
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ContextConfiguration(initializers = SecurityIT.ContextInitializer.class)
public class SecurityIT {

  private static ApiClient anApiClient(String token) {
    return TestUtils.anApiClient(token, ContextInitializer.SERVER_PORT);
  }

  @Test
  void get_dummy_data_ok() throws ApiException {
    ApiClient client = anApiClient("client_token");
    SecurityApi api = new SecurityApi(client);

    DummyComponent actual = api.checkDbHealth();
    DummyComponent expected = new DummyComponent();
    expected.setId("dummy_table_id");

    assertEquals(expected, actual);
  }

  static class ContextInitializer extends AbstractContextInitializer {
    private static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }

}
