package api.bpartners.annotator.endpoint.rest.controller.mapper;

import api.bpartners.annotator.endpoint.rest.model.Annotation;
import api.bpartners.annotator.repository.model.Annotation;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AnnotationMapper {
    private final LabelMapper labelMapper;
    private final PolygonMapper polygonMapper;

    public Annotation toRest(Annotation domain) {
        return new Annotation()
                .id(domain.getId())
                .label(labelMapper.toRest(domain.getLabel()))
                .taskId(domain.getTaskId())
                .userId(domain.getUserId())
                .polygon(polygonMapper.toRest(domain.getPolygon()));
    }

    public Annotation toDomain(Annotation rest) {
        return Annotation.builder()
                .id(rest.getId())
                .label(labelMapper.toDomain(rest.getLabel()))
                .taskId(rest.getTaskId())
                .userId(rest.getUserId())
                .polygon(polygonMapper.toDomain(rest.getPolygon()))
                .build();
    }
}
