package api.bpartners.annotator.service.geojobs;

import static java.net.http.HttpRequest.BodyPublishers.noBody;

import api.bpartners.annotator.model.exception.ApiException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GeoJobsApi {
  private static final String API_KEY_HEADER = "x-api-key";
  private final String apiKey;
  private final String apiUrl;

  public GeoJobsApi(
      @Value("${geo.jobs.api.key}") String apiKey, @Value("${geo.jobs.api.url}") String apiUrl) {
    this.apiKey = apiKey;
    this.apiUrl = apiUrl;
  }

  @SneakyThrows
  public void notify(String annotationJobId) {
    HttpClient client = HttpClient.newBuilder().build();
    HttpRequest request =
        HttpRequest.newBuilder()
            .POST(noBody())
            .uri(
                new URI(
                    String.format(
                        "%s/detectionJobs/%s/humanVerificationStatus", apiUrl, annotationJobId)))
            .header(API_KEY_HEADER, apiKey)
            .build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    log.info("response body {}", response.body());
    if (response.statusCode() != 200) {
      throw new ApiException(ApiException.ExceptionType.SERVER_EXCEPTION, response.body());
    }
  }
}
