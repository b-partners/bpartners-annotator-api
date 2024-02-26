package api.bpartners.annotator.endpoint.rest.controller.mapper;

import api.bpartners.annotator.endpoint.rest.model.Annotation;
import api.bpartners.annotator.endpoint.rest.model.AnnotationBaseFields;
import api.bpartners.annotator.endpoint.rest.validator.AnnotationValidator;
import api.bpartners.annotator.endpoint.rest.validator.CreateAnnotationValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AnnotationMapper {
  private final LabelMapper labelMapper;
  private final PolygonMapper polygonMapper;
  private final AnnotationValidator validator;
  private final CreateAnnotationValidator createAnnotationValidator;

  public Annotation toRest(api.bpartners.annotator.repository.model.Annotation domain) {
    return new Annotation()
        .id(domain.getId())
        .label(labelMapper.toRest(domain.getLabel()))
        .taskId(domain.getTaskId())
        .userId(domain.getUserId())
        .polygon(polygonMapper.toRest(domain.getPolygon()));
  }

  public api.bpartners.annotator.repository.model.Annotation toDomain(
      String annotationBatchId, Annotation rest) {
    validator.accept(rest);
    return api.bpartners.annotator.repository.model.Annotation.builder()
        .id(rest.getId())
        .label(labelMapper.toDomain(rest.getLabel()))
        .taskId(rest.getTaskId())
        .userId(rest.getUserId())
        .batchId(annotationBatchId)
        .polygon(polygonMapper.toDomain(rest.getPolygon()))
        .build();
  }

  public api.bpartners.annotator.repository.model.Annotation toDomain(
      String annotationBatchId, AnnotationBaseFields rest, String taskId) {
    createAnnotationValidator.accept(rest);
    return api.bpartners.annotator.repository.model.Annotation.builder()
        .id(rest.getId())
        .label(labelMapper.toDomain(rest.getLabel()))
        .taskId(taskId)
        .userId(rest.getUserId())
        .batchId(annotationBatchId)
        .polygon(polygonMapper.toDomain(rest.getPolygon()))
        .build();
  }
}
