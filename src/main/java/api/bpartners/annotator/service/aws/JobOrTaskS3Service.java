package api.bpartners.annotator.service.aws;

import static api.bpartners.annotator.model.exception.ApiException.ExceptionType.SERVER_EXCEPTION;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

import api.bpartners.annotator.model.CustomS3Object;
import api.bpartners.annotator.model.TokennedCustomS3ObjectList;
import api.bpartners.annotator.model.exception.ApiException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import javax.imageio.ImageIO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
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

  public CustomS3Object getCustomS3ObjectForImage(String bucketName, String key) {
    ResponseBytes<GetObjectResponse> objectAsBytes =
        client.getObjectAsBytes(GetObjectRequest.builder().bucket(bucketName).key(key).build());
    String responseContentType = objectAsBytes.response().contentType();
    if (!IMAGE_PNG_VALUE.equals(responseContentType)
        && !IMAGE_JPEG_VALUE.equals(responseContentType)) {
      throw new ApiException(
          SERVER_EXCEPTION,
          "only JPEG and PNG content-types are accepted for Tasks, correct following file"
              + " bucketName = "
              + bucketName
              + ", key = "
              + key);
    }

    byte[] bytes = objectAsBytes.asByteArray();
    ByteArrayInputStream bis = new ByteArrayInputStream(bytes);

    try {
      BufferedImage image = ImageIO.read(bis);
      int width = image.getWidth();
      int height = image.getHeight();

      return CustomS3Object.builder()
          .bucketName(bucketName)
          .key(key)
          .width(width)
          .height(height)
          .size(bytes.length / 1024)
          .build();
    } catch (IOException e) {
      throw new ApiException(SERVER_EXCEPTION, e);
    }
  }
}
