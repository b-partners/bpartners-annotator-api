package api.bpartners.annotator.endpoint.rest.controller;

import api.bpartners.annotator.endpoint.rest.controller.mapper.UserMapper;
import api.bpartners.annotator.endpoint.rest.model.CreateUser;
import api.bpartners.annotator.endpoint.rest.model.User;
import api.bpartners.annotator.model.BoundedPageSize;
import api.bpartners.annotator.model.PageFromOne;
import api.bpartners.annotator.service.UserService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class UserController {
  private final UserService userService;
  private final UserMapper mapper;

  @GetMapping("/users")
  public List<User> getUsers(
      @RequestParam(required = false) PageFromOne page,
      @RequestParam(required = false) BoundedPageSize pageSize) {
    return userService.findAll(page, pageSize).stream().map(mapper::toRest).toList();
  }

  @PostMapping("/users")
  public List<User> createUsers(@RequestBody List<CreateUser> users) {
    return userService.fireEvents(users.stream().map(mapper::toDomain).toList()).stream()
        .map(mapper::toRest)
        .toList();
  }
}
