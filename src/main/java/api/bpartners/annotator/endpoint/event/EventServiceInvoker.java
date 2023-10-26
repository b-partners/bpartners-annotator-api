package api.bpartners.annotator.endpoint.event;

import api.bpartners.annotator.endpoint.event.gen.JobCreated;
import api.bpartners.annotator.endpoint.event.model.TypedEvent;
import api.bpartners.annotator.service.JobCreatedService;
import java.io.Serializable;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
@Slf4j
public class EventServiceInvoker implements Consumer<TypedEvent> {
  private final JobCreatedService jobCreatedService;
  @Override
  public void accept(TypedEvent typedEvent) {
    Serializable payload = typedEvent.getPayload();
    if(JobCreated.class.getTypeName().equals(typedEvent.getTypeName())) {
        jobCreatedService.accept((JobCreated) payload);
    }else {
      log.error("Unexpected type for event={}", typedEvent);
    }
  }
}
