package api.bpartners.annotator.endpoint.rest.controller.mapper;

import api.bpartners.annotator.endpoint.rest.model.AnnotationNumberPerLabel;
import api.bpartners.annotator.endpoint.rest.model.CrupdateAnnotatedJob;
import api.bpartners.annotator.endpoint.rest.model.CrupdateJob;
import api.bpartners.annotator.endpoint.rest.model.Job;
import api.bpartners.annotator.endpoint.rest.security.AuthenticatedResourceProvider;
import api.bpartners.annotator.endpoint.rest.validator.CrupdateAnnotatedJobValidator;
import api.bpartners.annotator.endpoint.rest.validator.JobValidator;
import api.bpartners.annotator.service.AnnotationBatchService;
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
  private final CrupdateAnnotatedJobValidator crupdateAnnotatedJobValidator;
  private final AnnotationBatchService annotationBatchService;

  public Job toRest(api.bpartners.annotator.repository.model.Job domain) {
    String connectedUserId = authenticatedResourceProvider.getAuthenticatedUser().getId();
    List<AnnotationNumberPerLabel> annotationStatistics =
        annotationBatchService.getAnnotationStatistics(domain);
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
        .taskStatistics(domain.getTaskStatistics(connectedUserId))
        .annotationStatistics(annotationStatistics);
  }

  // use for rest list because annotationStatistics computing is a heavy task
  public Job toRestListComponent(api.bpartners.annotator.repository.model.Job domain) {
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
        .taskStatistics(domain.getTaskStatistics(connectedUserId))
        .annotationStatistics(List.of());
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

  public api.bpartners.annotator.repository.model.Job toDomain(CrupdateAnnotatedJob rest) {
    crupdateAnnotatedJobValidator.accept(rest);
    CrupdateJob converted = toCrupdateJob(rest);
    return toDomain(converted);
  }

  private CrupdateJob toCrupdateJob(CrupdateAnnotatedJob crupdateAnnotatedJob) {
    return new CrupdateJob()
        .id(crupdateAnnotatedJob.getId())
        .name(crupdateAnnotatedJob.getName())
        .bucketName(crupdateAnnotatedJob.getBucketName())
        .folderPath(crupdateAnnotatedJob.getFolderPath())
        .ownerEmail(crupdateAnnotatedJob.getOwnerEmail())
        .status(crupdateAnnotatedJob.getStatus())
        .labels(crupdateAnnotatedJob.getLabels())
        .teamId(crupdateAnnotatedJob.getTeamId())
        .imagesHeight(crupdateAnnotatedJob.getImagesHeight())
        .imagesWidth(crupdateAnnotatedJob.getImagesWidth())
        .type(crupdateAnnotatedJob.getType());
  }
}
