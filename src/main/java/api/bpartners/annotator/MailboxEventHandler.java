package api.bpartners.annotator;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MailboxEventHandler implements RequestHandler<SQSEvent, String> {
  @Override
  public String handleRequest(SQSEvent event, Context context) {
    log.info("Following received events : %s\n", event);
    List<SQSEvent.SQSMessage> messages = event.getRecords();
    log.info("Following received messages : %s\n", messages);

    //eventConsumer.accept(toAcknowledgeableTypedEvent(eventConf, sqsClient, messages));
    return "{message: ok}";
  }
}
