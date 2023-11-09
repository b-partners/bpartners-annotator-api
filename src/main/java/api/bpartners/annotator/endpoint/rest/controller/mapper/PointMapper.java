package api.bpartners.annotator.endpoint.rest.controller.mapper;

import api.bpartners.annotator.endpoint.rest.model.Point;
import api.bpartners.annotator.endpoint.rest.model.Polygon;
import api.bpartners.annotator.repository.model.Annotation;
import org.springframework.stereotype.Component;

@Component
public class PointMapper {
    public Point toRest(Annotation.Point domain) {
        return new Point()
                .x(domain.getX())
                .y(domain.getY());
    }

    public Annotation.Point toDomain(Point rest){
        return Annotation.Point.builder()
                .x(rest.getX())
                .y(rest.getY())
                .build();
    }
}
