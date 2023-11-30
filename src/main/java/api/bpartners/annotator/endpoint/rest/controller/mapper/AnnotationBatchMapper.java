package api.bpartners.annotator.endpoint.rest.controller.mapper;

import api.bpartners.annotator.endpoint.rest.model.Annotation;
import api.bpartners.annotator.endpoint.rest.model.AnnotationBatch;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AnnotationBatchMapper {
  private final AnnotationMapper annotationMapper;

  public AnnotationBatch toRest(api.bpartners.annotator.repository.model.AnnotationBatch domain) {
    List<Annotation> annotations =
        domain.getAnnotations().stream().map(annotationMapper::toRest).toList();
    return new AnnotationBatch().id(domain.getId()).annotations(annotations);
  }

  public api.bpartners.annotator.repository.model.AnnotationBatch toDomain(
      String userId, String taskId, AnnotationBatch rest) {
    return api.bpartners.annotator.repository.model.AnnotationBatch.builder()
        .id(rest.getId())
        .annotatorId(userId)
        .taskId(taskId)
        .build();
  }
}
