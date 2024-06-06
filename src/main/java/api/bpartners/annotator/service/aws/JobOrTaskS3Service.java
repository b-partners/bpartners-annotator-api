package api.bpartners.annotator.service.aws;

import api.bpartners.annotator.model.TokennedCustomS3ObjectList;
import java.net.URL;
import java.time.Duration;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Service
@AllArgsConstructor
public class JobOrTaskS3Service {
  /*
  /!\ CANNOT USE POJA BucketComponent because this service's use-case is too specific.
      URL Presigning might be do-able but we must let the user specify the bucketName in POJA IMPLEMENTATION
  /!\
   */
  private static final Integer MAX_KEYS = 1_000;
  private final S3Conf conf;
  private final S3Client client;

  public URL getPresignedUrl(String bucketName, String key) {
    try (S3Presigner urlPresigner = S3Presigner.builder().region(conf.getRegion()).build()) {
      PresignedGetObjectRequest presignedGetObjectRequest =
          urlPresigner.presignGetObject(
              builder ->
                  builder
                      .getObjectRequest(
                          GetObjectRequest.builder().bucket(bucketName).key(key).build())
                      .signatureDuration(Duration.ofHours(1)));
      return presignedGetObjectRequest.url();
    }
  }

  public TokennedCustomS3ObjectList getObjectKeys(
      String bucketName, String prefix, String continuationToken) {
    ListObjectsV2Response response =
        client.listObjectsV2(
            ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(prefix == null ? "" : prefix)
                .continuationToken(continuationToken)
                .maxKeys(MAX_KEYS)
                .build());

    return TokennedCustomS3ObjectList.builder()
        .objectsFilename(
            response.contents().stream()
                .map(S3Object::key)
                .map(key -> replacePrefix(key, prefix))
                .toList())
        .nextContinuationToken(
            response.nextContinuationToken() == null ? null : response.nextContinuationToken())
        .build();
  }

  private String replacePrefix(String original, String prefix) {
    return original.replace(prefix, "");
  }
}
