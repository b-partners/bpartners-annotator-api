package api.bpartners.annotator.endpoint.event.consumer.model;

import api.bpartners.annotator.PojaGenerated;

@PojaGenerated
public record TypedEvent(String typeName, Object payload) {}
