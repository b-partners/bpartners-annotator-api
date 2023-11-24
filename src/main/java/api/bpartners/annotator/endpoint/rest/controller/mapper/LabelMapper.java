package api.bpartners.annotator.endpoint.rest.controller.mapper;

import api.bpartners.annotator.endpoint.rest.model.Label;
import org.springframework.stereotype.Component;

@Component
public class LabelMapper {
  public Label toRest(api.bpartners.annotator.repository.model.Label domain) {
    if (domain == null) {
      return null;
    }
    return new Label().id(domain.getId()).name(domain.getName()).color(domain.getColor());
  }

  public api.bpartners.annotator.repository.model.Label toDomain(Label rest) {
    if (rest == null) {
      return null;
    }
    return api.bpartners.annotator.repository.model.Label.builder()
        .id(rest.getId())
        .name(rest.getName())
        .color(rest.getColor())
        .build();
  }
}
