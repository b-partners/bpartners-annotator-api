package api.bpartners.annotator.endpoint.event;

import api.bpartners.annotator.endpoint.event.model.TypedEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;

import static java.util.concurrent.Executors.newFixedThreadPool;

@Component
@Slf4j
public class EventConsumer implements Consumer<List<EventConsumer.AcknowledgeableTypedEvent>> {
  public static final String DETAIL_TYPE_PROPERTY = "detail-type";
  private static final int MAX_THREADS = 10;
  private static final ObjectMapper om = new ObjectMapper();
  private final Executor executor;
  private final EventServiceInvoker eventServiceInvoker;

  public EventConsumer(EventServiceInvoker eventServiceInvoker) {
    this.executor = newFixedThreadPool(MAX_THREADS);
    this.eventServiceInvoker = eventServiceInvoker;
  }

  public static List<AcknowledgeableTypedEvent> toAcknowledgeableTypedEvent(
      EventConf eventConf, SqsClient sqsClient, List<SQSEvent.SQSMessage> messages) {
    var res = new ArrayList<AcknowledgeableTypedEvent>();
    for (SQSEvent.SQSMessage message : messages) {
      TypedEvent typedEvent;
      try {
        typedEvent = toTypedEvent(message);
      } catch (Exception e) {
        log.error(e.getMessage());
        log.error("Message could not be unmarshalled, message : %s \n", message);
        continue;
      }
      AcknowledgeableTypedEvent event = new AcknowledgeableTypedEvent(
          typedEvent,
          () -> sqsClient.deleteMessage(DeleteMessageRequest.builder()
              .queueUrl(eventConf.getSqsQueue())
              .receiptHandle(message.getReceiptHandle())
              .build()));
      res.add(event);
    }
    return res;
  }

  private static TypedEvent toTypedEvent(SQSEvent.SQSMessage message)
      throws JsonProcessingException {
    TypedEvent typedEvent = null;
    TypeReference<Map<String, Object>> typeRef = new TypeReference<>() {
    };
    Map<String, Object> body = om.readValue(message.getBody(), typeRef);
    String typeName = body.get(DETAIL_TYPE_PROPERTY).toString();
    // TODO: implement according to event type
    return typedEvent;
  }

  @Override
  public void accept(List<AcknowledgeableTypedEvent> ackTypedEvents) {
    for (AcknowledgeableTypedEvent ackTypedEvent : ackTypedEvents) {
      executor.execute(() -> {
        eventServiceInvoker.accept(ackTypedEvent.getTypedEvent());
        ackTypedEvent.ack();
      });
    }
  }

  @AllArgsConstructor
  public static class AcknowledgeableTypedEvent {
    @Getter
    private final TypedEvent typedEvent;
    private final Runnable acknowledger;

    public void ack() {
      acknowledger.run();
    }
  }
}
