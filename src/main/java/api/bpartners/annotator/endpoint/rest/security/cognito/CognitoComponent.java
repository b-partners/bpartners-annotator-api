package api.bpartners.annotator.endpoint.rest.security.cognito;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import java.text.ParseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;


@Slf4j
@Component
public class CognitoComponent {

  public static final String BASIC_AUTH_PREFIX = "Basic ";
  private final CognitoConf cognitoConf;
  private final CognitoIdentityProviderClient cognitoClient;
  private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

  public CognitoComponent(
      CognitoConf cognitoConf,
      CognitoIdentityProviderClient cognitoClient) {
    this.cognitoConf = cognitoConf;
    this.cognitoClient = cognitoClient;
  }

  public String getEmailByToken(String idToken) {
    JWTClaimsSet claims;
    try {
      claims = cognitoConf.getCognitoJwtProcessor().process(idToken, null);
    } catch (ParseException | BadJOSEException | JOSEException e) {
      /* From Javadoc:
         ParseException – If the string couldn't be parsed to a valid JWT.
         BadJOSEException – If the JWT is rejected.
         JOSEException – If an internal processing exception is encountered. */
      return null;
    }

    return isClaimsSetValid(claims) ? getEmail(claims) : null;
  }

  private boolean isClaimsSetValid(JWTClaimsSet claims) {
    return claims.getIssuer().equals(cognitoConf.getUserPoolUrl());
  }

  private String getEmail(JWTClaimsSet claims) {
    return claims.getClaims().get("email").toString();
  }
}
