package api.bpartners.annotator;

import api.bpartners.annotator.endpoint.event.EventConf;
import api.bpartners.annotator.endpoint.event.EventConsumer;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.sqs.SqsClient;

import static api.bpartners.annotator.endpoint.event.EventConsumer.toAcknowledgeableTypedEvent;

@Slf4j
public class MailboxEventHandler implements RequestHandler<SQSEvent, String> {
  private final EventConsumer eventConsumer;

  private final EventConf eventConf;
  private final SqsClient sqsClient;

  public MailboxEventHandler(EventConf eventConf, EventConsumer eventConsumer) {
    this.eventConf = eventConf;
    this.eventConsumer = eventConsumer;
    this.sqsClient = eventConf.getSqsClient();
  }

  @Override
  public String handleRequest(SQSEvent event, Context context) {
    log.info("Following received events : %s\n", event);
    List<SQSEvent.SQSMessage> messages = event.getRecords();
    log.info("Following received messages : %s\n", messages);

    eventConsumer.accept(toAcknowledgeableTypedEvent(eventConf, sqsClient, messages));
    return "{message: ok}";
  }
}
