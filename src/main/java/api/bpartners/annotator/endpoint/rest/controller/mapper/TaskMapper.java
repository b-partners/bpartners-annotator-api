package api.bpartners.annotator.endpoint.rest.controller.mapper;

import api.bpartners.annotator.endpoint.rest.model.Task;
import api.bpartners.annotator.endpoint.rest.model.TaskStatistics;
import api.bpartners.annotator.endpoint.rest.model.UpdateTask;
import api.bpartners.annotator.endpoint.rest.model.UserTask;
import api.bpartners.annotator.endpoint.rest.security.AuthenticatedResourceProvider;
import api.bpartners.annotator.service.aws.S3Service;
import java.net.URL;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TaskMapper {
  private final TaskStatusMapper statusMapper;
  private final S3Service fileService;
  private final AuthenticatedResourceProvider authenticatedResourceProvider;

  public Task toRest(api.bpartners.annotator.repository.model.Task domain) {
    if (domain == null) {
      return null;
    }
    URL presignedUrl =
        fileService.getPresignedUrl(
            domain.getJob().getBucketName(),
            domain.getJob().getFolderPath() + domain.getFilename());
    return new Task()
        .id(domain.getId())
        .status(statusMapper.toRest(domain.getStatus()))
        .imageURI(presignedUrl.toString())
        .userId(domain.getUserId());
  }

  public UserTask toRestUserTask(api.bpartners.annotator.repository.model.Task domain) {
    if (domain == null) {
      return null;
    }
    URL presignedUrl =
        fileService.getPresignedUrl(
            domain.getJob().getBucketName(),
            domain.getJob().getFolderPath() + domain.getFilename());
    TaskStatistics taskStatistics =
        new TaskStatistics()
            .remainingTasks(domain.getJob().getRemainingTasksNumber())
            .totalTasks((long) domain.getJob().getTasks().size())
            .completedTasksByUserId(
                domain
                    .getJob()
                    .getTasksCompletedByUserId(
                        authenticatedResourceProvider.getAuthenticatedUser().getId()));
    return new UserTask()
        .id(domain.getId())
        .imageUri(presignedUrl.toString())
        .statistics(taskStatistics)
        .status(statusMapper.toRest(domain.getStatus()))
        .userId(domain.getUserId());
  }

  public api.bpartners.annotator.repository.model.Task toDomain(UpdateTask rest) {
    return api.bpartners.annotator.repository.model.Task.builder()
        .id(rest.getId())
        .userId(rest.getUserId())
        .job(null)
        .filename(null)
        .status(statusMapper.toDomain(rest.getStatus()))
        .build();
  }
}
