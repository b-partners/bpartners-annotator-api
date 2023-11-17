package api.bpartners.annotator.service.aws;

import static api.bpartners.annotator.model.exception.ApiException.ExceptionType.SERVER_EXCEPTION;

import api.bpartners.annotator.model.exception.ApiException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.Body;
import software.amazon.awssdk.services.ses.model.Content;
import software.amazon.awssdk.services.ses.model.Message;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;

@Service
@AllArgsConstructor
public class SesService {
  private final SesClient client;
  private final SesConf sesConf;

  public void sendEmail(String recipient, String subject, String htmlBody) {
    Message message =
        Message.builder()
            .subject(Content.builder().data(subject).build())
            .body(Body.builder().html(Content.builder().data(htmlBody).build()).build())
            .build();
    SendEmailRequest emailRequest =
        SendEmailRequest.builder()
            .source(sesConf.getSesSource())
            .destination(destination -> destination.toAddresses(recipient))
            .message(message)
            .build();

    try {
      client.sendEmail(emailRequest);
    } catch (AwsServiceException | SdkClientException exception) {
      throw new ApiException(SERVER_EXCEPTION, exception.getMessage());
    }
  }
}
