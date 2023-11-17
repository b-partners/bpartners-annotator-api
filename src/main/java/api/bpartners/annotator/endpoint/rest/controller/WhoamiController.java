package api.bpartners.annotator.endpoint.rest.controller;

import api.bpartners.annotator.endpoint.rest.controller.mapper.UserMapper;
import api.bpartners.annotator.endpoint.rest.model.Whoami;
import api.bpartners.annotator.endpoint.rest.security.model.Principal;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class WhoamiController {
  private final UserMapper userMapper;

  @GetMapping("/whoami")
  public Whoami whoami(@AuthenticationPrincipal Principal principal) {
    return new Whoami().user(userMapper.toRest(principal.getUser()));
  }
}
