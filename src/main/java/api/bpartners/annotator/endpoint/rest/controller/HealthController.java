package api.bpartners.annotator.endpoint.rest.controller;

import api.bpartners.annotator.endpoint.rest.model.DummyComponent;
import api.bpartners.annotator.service.DummyComponentService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class HealthController {
  private final DummyComponentService service;

  private DummyComponent toRest(api.bpartners.annotator.repository.jpa.model.DummyComponent domain) {
    DummyComponent rest = new DummyComponent();
    rest.setId(domain.getId());
    return rest;
  }

  @GetMapping("/ping")
  public String checkHealth() {
    return "pong";
  }

  @GetMapping("/dummy")
  public DummyComponent checkDbHealth() {
    return toRest(service.getDummyTestData());
  }
}
