package api.bpartners.annotator.endpoint.event;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;

import api.bpartners.annotator.PojaGenerated;
import api.bpartners.annotator.conf.FacadeIT;
import api.bpartners.annotator.endpoint.event.consumer.EventConsumer;
import api.bpartners.annotator.endpoint.event.consumer.model.ConsumableEvent;
import api.bpartners.annotator.endpoint.event.consumer.model.TypedEvent;
import api.bpartners.annotator.endpoint.event.model.UuidCreated;
import api.bpartners.annotator.repository.DummyUuidRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@PojaGenerated
@SuppressWarnings("all")
class EventConsumerIT extends FacadeIT {

  @Autowired EventConsumer subject;
  @Autowired DummyUuidRepository dummyUuidRepository;
  @Autowired ObjectMapper om;

  @Test
  void uuid_created_is_persisted() throws InterruptedException, JsonProcessingException {
    var uuid = randomUUID().toString();
    var uuidCreated = UuidCreated.builder().uuid(uuid).build();
    var payloadReceived = om.readValue(om.writeValueAsString(uuidCreated), UuidCreated.class);

    subject.accept(
        List.of(
            new ConsumableEvent(
                new TypedEvent(
                    "api.bpartners.annotator.endpoint.event.model.UuidCreated", payloadReceived),
                () -> {},
                () -> {})));

    Thread.sleep(2_000);
    var saved = dummyUuidRepository.findById(uuid).orElseThrow();
    assertEquals(uuid, saved.getId());
  }
}
