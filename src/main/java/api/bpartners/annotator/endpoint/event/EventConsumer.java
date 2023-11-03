package api.bpartners.annotator.endpoint.event;

import api.bpartners.annotator.endpoint.event.gen.JobCreated;
import api.bpartners.annotator.endpoint.event.model.TypedEvent;
import api.bpartners.annotator.endpoint.event.model.TypedJobCreated;
import api.bpartners.annotator.model.exception.BadRequestException;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;

@Component
@Slf4j
public class EventConsumer implements Consumer<List<EventConsumer.AcknowledgeableTypedEvent>> {
  public static final String DETAIL_TYPE_PROPERTY = "detail-type";
  private static final ObjectMapper om = new ObjectMapper();
  private static final String DETAIL_ROPERTY = "detail";
  private final EventServiceInvoker eventServiceInvoker;

  public EventConsumer(EventServiceInvoker eventServiceInvoker) {
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
    TypedEvent typedEvent;
    TypeReference<Map<String, Object>> typeRef = new TypeReference<>() {
    };
    Map<String, Object> body = om.readValue(message.getBody(), typeRef);
    String typeName = body.get(DETAIL_TYPE_PROPERTY).toString();
    if (JobCreated.class.getTypeName().equals(typeName)) {
      JobCreated jobCreated = om.convertValue(body.get(DETAIL_ROPERTY), JobCreated.class);
      typedEvent = new TypedJobCreated(jobCreated);
    } else {
      throw new BadRequestException("Unexpected message type for message=" + message);
    }
    return typedEvent;
  }

  @Override
  public void accept(List<AcknowledgeableTypedEvent> ackTypedEvents) {
    for (AcknowledgeableTypedEvent ackTypedEvent : ackTypedEvents) {
      eventServiceInvoker.accept(ackTypedEvent.getTypedEvent());
      ackTypedEvent.ack();
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
