package api.bpartners.annotator.integration.conf.utils;

import static api.bpartners.annotator.integration.conf.utils.TestMocks.JOE_DOE_EMAIL;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.JOE_DOE_TOKEN;
import static api.bpartners.annotator.integration.conf.utils.TestMocks.MOCK_PRESIGNED_URL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import api.bpartners.annotator.endpoint.rest.client.ApiClient;
import api.bpartners.annotator.endpoint.rest.client.ApiException;
import api.bpartners.annotator.endpoint.rest.security.cognito.CognitoComponent;
import api.bpartners.annotator.service.aws.JobOrTaskS3Service;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.jupiter.api.function.Executable;

public class TestUtils {
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

  public static void setUpCognito(CognitoComponent cognitoComponent) {
    when(cognitoComponent.getEmailByToken(JOE_DOE_TOKEN)).thenReturn(JOE_DOE_EMAIL);
  }

  public static void setUpS3Service(JobOrTaskS3Service fileService) throws MalformedURLException {
    when(fileService.getPresignedUrl(any(String.class), any(String.class)))
        .thenReturn(new URL(MOCK_PRESIGNED_URL));
  }

  public static void assertThrowsForbiddenException(Executable executable, String message) {
    ApiException apiException = assertThrows(ApiException.class, executable);
    String responseBody = apiException.getResponseBody();
    assertEquals(
        "{" + "\"type\":\"403 FORBIDDEN\"," + "\"message\":\"" + message + "\"}", responseBody);
  }

  public static void assertThrowsBadRequestException(Executable executable, String message) {
    ApiException apiException = assertThrows(ApiException.class, executable);
    String responseBody = apiException.getResponseBody();
    assertEquals(
        "{" + "\"type\":\"400 BAD_REQUEST\"," + "\"message\":\"" + message + "\"}", responseBody);
  }
}
