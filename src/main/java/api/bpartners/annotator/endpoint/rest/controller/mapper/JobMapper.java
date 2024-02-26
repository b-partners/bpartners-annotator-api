package api.bpartners.annotator.endpoint.rest.controller.mapper;

import api.bpartners.annotator.endpoint.rest.model.CrupdateJob;
import api.bpartners.annotator.endpoint.rest.model.Job;
import api.bpartners.annotator.endpoint.rest.security.AuthenticatedResourceProvider;
import api.bpartners.annotator.endpoint.rest.validator.JobValidator;
import api.bpartners.annotator.repository.model.User;
import api.bpartners.annotator.service.UserService;
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
  private final UserService userService;

  public Job toRest(api.bpartners.annotator.repository.model.Job domain) {
    var geoJobsUserIds = userService.getAllGeoJobsUsers().stream().map(User::getId).toList();
    String connectedUserId = authenticatedResourceProvider.getAuthenticatedUser().getId();
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
        .imagesHeight(domain.getImagesHeight())
        .imagesWidth(domain.getImagesWidth())
        .taskStatistics(domain.getTaskStatistics(connectedUserId, geoJobsUserIds));
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
        .imagesHeight(rest.getImagesHeight())
        .imagesWidth(rest.getImagesWidth())
        .labels(rest.getLabels().stream().map(labelMapper::toDomain).toList())
        .build();
  }
}
