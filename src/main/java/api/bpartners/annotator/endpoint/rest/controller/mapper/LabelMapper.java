package api.bpartners.annotator.endpoint.rest.controller.mapper;

import api.bpartners.annotator.endpoint.rest.model.Label;
import org.springframework.stereotype.Component;

@Component
public class LabelMapper {
  public Label toRest(api.bpartners.annotator.repository.jpa.model.Label domain) {
    return new Label()
        .id(domain.getId())
        .name(domain.getName());
  }

  public api.bpartners.annotator.repository.jpa.model.Label toDomain(Label rest) {
    return api.bpartners.annotator.repository.jpa.model.Label.builder()
        .id(rest.getId())
        .name(rest.getName())
        .build();
  }
}
