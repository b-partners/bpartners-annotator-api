package api.bpartners.annotator.endpoint.rest.controller.mapper;

import api.bpartners.annotator.endpoint.rest.model.CrupdateJob;
import api.bpartners.annotator.endpoint.rest.model.Job;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class JobMapper {
  private final JobStatusMapper statusMapper;
  private final LabelMapper labelMapper;

  public Job toRest(api.bpartners.annotator.repository.jpa.model.Job domain) {
    return new Job()
        .id(domain.getId())
        .remainingTasks(0)
        .bucketPath(domain.getBucketPath())
        .status(statusMapper.toRest(domain.getStatus()))
        .labels(domain.getLabels().stream().map(labelMapper::toRest).toList())
        .teamId(domain.getTeamId());
  }

  public api.bpartners.annotator.repository.jpa.model.Job toDomain(CrupdateJob rest) {
    return api.bpartners.annotator.repository.jpa.model.Job.builder()
        .id(rest.getId())
        .status(statusMapper.toDomain(rest.getStatus()))
        .bucketPath(rest.getBucketPath())
        .teamId(rest.getTeamId())
        .labels(rest.getLabels().stream().map(labelMapper::toDomain).toList())
        .build();
  }
}
