package api.bpartners.annotator.endpoint.rest.controller.mapper;

import api.bpartners.annotator.endpoint.rest.model.Polygon;
import api.bpartners.annotator.repository.model.Annotation;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PolygonMapper {
    private final PointMapper pointMapper;

    public Polygon toRest(Annotation.Polygon polygon) {
        return new Polygon()
                .points(
                        polygon.getPoints()
                                .stream()
                                .map(pointMapper::toRest)
                                .toList()
                );
    }

    public Annotation.Polygon toDomain(Polygon rest) {
        return Annotation.Polygon.builder()
                .points(rest.getPoints().stream().map(pointMapper::toDomain).toList())
                .build();
    }
}
