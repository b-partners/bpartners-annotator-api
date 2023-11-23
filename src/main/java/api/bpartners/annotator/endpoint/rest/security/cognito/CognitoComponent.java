package api.bpartners.annotator.endpoint.rest.security.cognito;

import static api.bpartners.annotator.model.exception.ApiException.ExceptionType.SERVER_EXCEPTION;

import api.bpartners.annotator.model.exception.ApiException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import java.text.ParseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminAddUserToGroupRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminAddUserToGroupResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CreateGroupRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CreateGroupResponse;

@Slf4j
@Component
public class CognitoComponent {

  public static final String BASIC_AUTH_PREFIX = "Basic ";
  private final CognitoConf cognitoConf;
  private final CognitoIdentityProviderClient cognitoClient;
  private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

  public CognitoComponent(CognitoConf cognitoConf, CognitoIdentityProviderClient cognitoClient) {
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

  public String createGroup(String groupName) {
    CreateGroupRequest request =
        CreateGroupRequest.builder()
            .groupName(groupName)
            .userPoolId(cognitoConf.getUserPoolId())
            .build();

    CreateGroupResponse response = cognitoClient.createGroup(request);
    if (response == null || response.group() == null || response.group().groupName() == null) {
      throw new ApiException(SERVER_EXCEPTION, "Cognito response was: " + response);
    }
    log.info("Group with name {} is successfully created.", response.group().groupName());
    return response.group().groupName();
  }

  public void addUserToGroup(String groupName, String username) {
    AdminAddUserToGroupRequest addUserToGroupRequest =
        AdminAddUserToGroupRequest.builder()
            .userPoolId(cognitoConf.getUserPoolId())
            .username(username)
            .groupName(groupName)
            .build();

    AdminAddUserToGroupResponse response = cognitoClient.adminAddUserToGroup(addUserToGroupRequest);
    if (response == null) {
      throw new ApiException(
          SERVER_EXCEPTION, "Error on adding user " + username + " to group " + groupName);
    }
    log.info("User {} successfully added to group {}.", username, groupName);
  }

  public String createUser(String email, String groupName) {
    AdminCreateUserRequest createRequest =
        AdminCreateUserRequest.builder()
            .userPoolId(cognitoConf.getUserPoolId())
            .username(email)
            // TODO: add test to ensure it has properly been set
            .userAttributes(
                AttributeType.builder().name("email").value(email).build(),
                AttributeType.builder().name("email_verified").value("true").build())
            .build();

    AdminCreateUserResponse createResponse = cognitoClient.adminCreateUser(createRequest);
    if (createResponse == null
        || createResponse.user() == null
        || createResponse.user().username().isBlank()) {
      throw new ApiException(SERVER_EXCEPTION, "Cognito response: " + createResponse);
    }
    String createdUser = createResponse.user().username();
    log.info("User with name {} is successfully created.", createdUser);
    if (groupName != null && !groupName.isEmpty()) {
      addUserToGroup(groupName, email);
    }
    return createdUser;
  }

  private boolean isClaimsSetValid(JWTClaimsSet claims) {
    return claims.getIssuer().equals(cognitoConf.getUserPoolUrl());
  }

  private String getEmail(JWTClaimsSet claims) {
    return claims.getClaims().get("email").toString();
  }
}
