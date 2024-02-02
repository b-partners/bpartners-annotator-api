package api.bpartners.annotator.endpoint.rest.controller.mapper;

import api.bpartners.annotator.endpoint.rest.model.CrupdateJob;
import api.bpartners.annotator.endpoint.rest.model.Job;
import api.bpartners.annotator.endpoint.rest.model.TaskStatistics;
import api.bpartners.annotator.endpoint.rest.security.AuthenticatedResourceProvider;
import api.bpartners.annotator.endpoint.rest.validator.JobValidator;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class JobMapper {
  private final JobStatusMapper statusMapper;
  private final LabelMapper labelMapper;
  private final JobValidator validator;
  private final AuthenticatedResourceProvider authenticatedResourceProvider;

  public Job toRest(api.bpartners.annotator.repository.model.Job domain) {
    String connectedUserId = authenticatedResourceProvider.getAuthenticatedUser().getId();
    TaskStatistics taskStatistics =
        new TaskStatistics()
            .completedTasksByUserId(domain.getTasksCompletedByUserId(connectedUserId))
            .totalTasks((long) domain.getTasks().size())
            .remainingTasks(domain.getRemainingTasksNumber())
            .remainingTasksForUserId(domain.getRemainingTasksForUserId(connectedUserId));
    return new Job()
        .id(domain.getId())
        .name(domain.getName())
        .bucketName(domain.getBucketName())
        .folderPath(domain.getFolderPath())
        .ownerEmail(domain.getOwnerEmail())
        .status(statusMapper.toRest(domain.getStatus()))
        .labels(domain.getLabels().stream().map(labelMapper::toRest).toList())
        .teamId(domain.getTeamId())
        .type(domain.getType())
        .taskStatistics(taskStatistics);
  }

  public api.bpartners.annotator.repository.model.Job toDomain(CrupdateJob rest) {
    validator.accept(rest);
    return api.bpartners.annotator.repository.model.Job.builder()
        .id(rest.getId())
        .name(rest.getName())
        .tasks(List.of())
        .status(statusMapper.toDomain(rest.getStatus()))
        .bucketName(rest.getBucketName())
        .folderPath(rest.getFolderPath())
        .ownerEmail(rest.getOwnerEmail())
        .teamId(rest.getTeamId())
        .type(rest.getType())
        .labels(rest.getLabels().stream().map(labelMapper::toDomain).toList())
        .build();
  }
}
