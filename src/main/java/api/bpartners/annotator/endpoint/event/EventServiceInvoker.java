package api.bpartners.annotator.endpoint.event;

import api.bpartners.annotator.endpoint.event.model.TypedEvent;
import java.io.Serializable;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
@Slf4j
public class EventServiceInvoker implements Consumer<TypedEvent> {
  @Override
  public void accept(TypedEvent event) {
    Serializable payload = event.getPayload();
    log.error("Unexpected type for event={}", event);
  }
}
