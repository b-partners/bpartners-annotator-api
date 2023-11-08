package api.bpartners.annotator.endpoint.rest.controller.mapper;

import api.bpartners.annotator.endpoint.rest.model.Label;
import api.bpartners.annotator.repository.model.Label;
import org.springframework.stereotype.Component;

@Component
public class LabelMapper {
  public Label toRest(Label domain) {
    return new Label()
        .id(domain.getId())
        .name(domain.getName());
  }

  public Label toDomain(Label rest) {
    return Label.builder()
        .id(rest.getId())
        .name(rest.getName())
        .build();
  }
}
