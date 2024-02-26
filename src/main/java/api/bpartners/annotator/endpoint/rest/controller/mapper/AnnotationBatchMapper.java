package api.bpartners.annotator.endpoint.rest.controller.mapper;

import api.bpartners.annotator.endpoint.rest.model.Annotation;
import api.bpartners.annotator.endpoint.rest.model.AnnotationBatch;
import api.bpartners.annotator.endpoint.rest.model.CreateAnnotationBatch;
import api.bpartners.annotator.service.TaskService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AnnotationBatchMapper {
  private final AnnotationMapper annotationMapper;
  private final TaskService taskService;

  public AnnotationBatch toRest(api.bpartners.annotator.repository.model.AnnotationBatch domain) {
    List<Annotation> annotations =
        domain.getAnnotations().stream().map(annotationMapper::toRest).toList();
    return new AnnotationBatch()
        .id(domain.getId())
        .annotations(annotations)
        .creationDatetime(domain.getCreationTimestamp());
  }

  public api.bpartners.annotator.repository.model.AnnotationBatch toDomain(
      String userId, String taskId, AnnotationBatch rest) {
    return api.bpartners.annotator.repository.model.AnnotationBatch.builder()
        .id(rest.getId())
        .annotatorId(userId)
        .annotations(
            rest.getAnnotations().stream()
                .map(restAnnotation -> annotationMapper.toDomain(rest.getId(), restAnnotation))
                .toList())
        .task(taskService.getById(taskId))
        .creationTimestamp(rest.getCreationDatetime())
        .build();
  }

  public api.bpartners.annotator.repository.model.AnnotationBatch toDomain(
      String userId, String taskId, CreateAnnotationBatch rest) {
    String id = rest.getId();
    return api.bpartners.annotator.repository.model.AnnotationBatch.builder()
        .id(id)
        .annotatorId(userId)
        .annotations(
            rest.getAnnotations().stream()
                .map(createAnnotation -> annotationMapper.toDomain(id, createAnnotation, taskId))
                .toList())
        .task(taskService.getById(taskId))
        .creationTimestamp(rest.getCreationDatetime())
        .build();
  }
}
