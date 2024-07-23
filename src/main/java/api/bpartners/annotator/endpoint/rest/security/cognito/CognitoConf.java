package api.bpartners.annotator.endpoint.rest.security.cognito;

import static api.bpartners.annotator.endpoint.rest.security.JWTConf.getContextConfigurableJWTProcessor;
import static api.bpartners.annotator.model.exception.ApiException.ExceptionType.SERVER_EXCEPTION;

import api.bpartners.annotator.endpoint.rest.security.JWTConf;
import api.bpartners.annotator.model.exception.ApiException;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.ResourceRetriever;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import java.net.MalformedURLException;
import java.net.URL;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

@Configuration
@Getter
public class CognitoConf {

  private final String region;
  private final String userPoolId;
  private final JWTConf jwtConf;
  private final String domain;
  private final String clientId;
  private final String clientSecret;

  public CognitoConf(
      @Value("${aws.region}") String region,
      @Value("${aws.cognito.userPool.id}") String userPoolId,
      @Value("${aws.cognito.userPool.domain}") String domain,
      @Value("${aws.cognito.userPool.clientId}") String clientId,
      @Value("${aws.cognito.userPool.clientSecret}") String clientSecret,
      JWTConf jwtConf) {
    this.region = region;
    this.userPoolId = userPoolId;
    this.jwtConf = jwtConf;
    this.domain = domain;
    this.clientId = clientId;
    this.clientSecret = clientSecret;
  }

  @Bean
  public ConfigurableJWTProcessor<SecurityContext> getCognitoJwtProcessor() {
    ResourceRetriever resourceRetriever =
        new DefaultResourceRetriever(jwtConf.getConnectTimeout(), jwtConf.getReadTimeout());
    URL jwkUrl = getCognitoJwksUrlFormat();
    return getContextConfigurableJWTProcessor(resourceRetriever, jwkUrl, jwtConf.getRs256());
  }

  @Bean
  public CognitoIdentityProviderClient getCognitoClient() {
    AwsBasicCredentials awsCreds = AwsBasicCredentials.create(clientId, clientSecret);
    return CognitoIdentityProviderClient.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
        .build();
  }

  public String getUserPoolUrl() {
    return String.format("https://cognito-idp.%s.amazonaws.com/%s", region, userPoolId);
  }

  private URL getCognitoJwksUrlFormat() {
    try {
      return new URL(getUserPoolUrl() + "/.well-known/jwks.json");
    } catch (MalformedURLException e) {
      throw new ApiException(SERVER_EXCEPTION, e);
    }
  }

  public String getOauthUrl() {
    return domain + "/oauth2/token";
  }
}
