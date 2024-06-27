package api.bpartners.annotator.endpoint.event.consumer.model;

import api.bpartners.annotator.PojaGenerated;
import api.bpartners.annotator.endpoint.event.model.PojaEvent;

@PojaGenerated
@SuppressWarnings("all")
public record TypedEvent(String typeName, PojaEvent payload) {}
