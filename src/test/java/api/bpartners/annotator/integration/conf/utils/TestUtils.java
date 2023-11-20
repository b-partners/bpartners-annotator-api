package api.bpartners.annotator.integration.conf.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import api.bpartners.annotator.endpoint.rest.client.ApiClient;
import api.bpartners.annotator.endpoint.rest.client.ApiException;
import api.bpartners.annotator.endpoint.rest.security.cognito.CognitoComponent;
import java.io.IOException;
import java.net.ServerSocket;
import org.junit.jupiter.api.function.Executable;

public class TestUtils {
  public static final String ADMIN_API_KEY = "dummy";
  public static final String JOE_DOE_TOKEN = "joe_doe_token";
  public static final String JOE_DOE_EMAIL = "joe@email.com";

  public static ApiClient anApiClient(String token, String apiKey, int serverPort) {
    ApiClient client = new ApiClient();
    client.setScheme("http");
    client.setHost("localhost");
    client.setPort(serverPort);
    if (apiKey == null) {
      client.setRequestInterceptor(
          httpRequestBuilder -> httpRequestBuilder.header("Authorization", "Bearer " + token));
    } else {
      client.setRequestInterceptor(
          httpRequestBuilder -> httpRequestBuilder.header("x-api-key", "dummy"));
    }
    return client;
  }

  public static int anAvailableRandomPort() {
    try {
      return new ServerSocket(0).getLocalPort();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void setUpCognito(CognitoComponent cognitoComponent) {
    when(cognitoComponent.getEmailByToken(JOE_DOE_TOKEN)).thenReturn(JOE_DOE_EMAIL);
  }

  public static void assertThrowsForbiddenException(Executable executable) {
    ApiException apiException = assertThrows(ApiException.class, executable);
    String responseBody = apiException.getResponseBody();
    assertEquals("{"
        + "\"type\":\"403 FORBIDDEN\","
        + "\"message\":\"Bad credentials\"}", responseBody);
  }

  public static void assertThrowsBadRequestException(Executable executable, String message){
    ApiException apiException = assertThrows(ApiException.class, executable);
    String responseBody = apiException.getResponseBody();
    assertEquals("{"
            + "\"type\":\"400 BAD_REQUEST\","
            + "\"message\":\""+message+"\"}", responseBody);
  }
}
