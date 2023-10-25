package api.bpartners.annotator.service.aws;

import java.net.URL;
import java.time.Duration;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Service
@AllArgsConstructor
public class S3Service {
  private final S3Conf conf;
  private final S3Client client;

  public URL getPresignedUrl(String bucketName, String key) {
    try (S3Presigner urlPresigner = S3Presigner.builder()
        .region(conf.getRegion())
        .build();) {

      PresignedGetObjectRequest presignedGetObjectRequest = urlPresigner
          .presignGetObject(
              builder -> builder
                  .getObjectRequest(
                      GetObjectRequest.builder()
                          .bucket(bucketName)
                          .key(key)
                          .build()
                  )
                  .signatureDuration(Duration.ofHours(1))
          );
      return presignedGetObjectRequest.url();
    }
  }
}
