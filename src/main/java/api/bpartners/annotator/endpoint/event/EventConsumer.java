package api.bpartners.annotator.endpoint.event;

import api.bpartners.annotator.endpoint.event.model.TypedEvent;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

import static java.util.concurrent.Executors.newFixedThreadPool;

@Component
public class EventConsumer implements Consumer<List<EventConsumer.AcknowledgeableTypedEvent>> {
  private static final int MAX_THREADS = 10;
  private final Executor executor;
  private final EventServiceInvoker eventServiceInvoker;

  public EventConsumer(EventServiceInvoker eventServiceInvoker) {
    this.executor = newFixedThreadPool(MAX_THREADS);
    this.eventServiceInvoker = eventServiceInvoker;
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
