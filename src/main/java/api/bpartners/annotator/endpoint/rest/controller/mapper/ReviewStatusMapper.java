package api.bpartners.annotator.endpoint.rest.controller.mapper;

import static api.bpartners.annotator.endpoint.rest.model.ReviewStatus.*;
import static api.bpartners.annotator.endpoint.rest.model.ReviewStatus.REJECTED;

import api.bpartners.annotator.repository.model.enums.ReviewStatus;
import org.springframework.stereotype.Component;

@Component
public class ReviewStatusMapper {
  public api.bpartners.annotator.endpoint.rest.model.ReviewStatus toRest(ReviewStatus domain) {
    return switch (domain) {
      case ACCEPTED -> ACCEPTED;
      case REJECTED -> REJECTED;
    };
  }

  public ReviewStatus toDomain(api.bpartners.annotator.endpoint.rest.model.ReviewStatus rest) {
    return switch (rest) {
      case ACCEPTED -> ReviewStatus.ACCEPTED;
      case REJECTED -> ReviewStatus.REJECTED;
    };
  }
}
