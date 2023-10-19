package api.bpartners.annotator.endpoint.rest.controller.mapper;

import api.bpartners.annotator.endpoint.rest.model.Task;
import api.bpartners.annotator.endpoint.rest.model.UpdateTask;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TaskMapper {
  private final TaskStatusMapper statusMapper;

  public Task toRest(api.bpartners.annotator.repository.jpa.model.Task domain) {
    if (domain == null) {
      return null;
    }
    return new Task()
        .id(domain.getId())
        .status(statusMapper.toRest(domain.getStatus()))
        .imageURI(domain.getImageURI())
        .userId(domain.getUserId());
  }

  public api.bpartners.annotator.repository.jpa.model.Task toDomain(UpdateTask rest) {
    return api.bpartners.annotator.repository.jpa.model.Task.builder()
        .id(rest.getId())
        .userId(rest.getUserId())
        .job(null)
        .s3ImageKey(null)
        .status(statusMapper.toDomain(rest.getStatus()))
        .build();
  }
}
