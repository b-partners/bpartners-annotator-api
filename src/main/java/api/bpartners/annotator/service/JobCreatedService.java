package api.bpartners.annotator.service;

import api.bpartners.annotator.endpoint.event.EventProducer;
import api.bpartners.annotator.endpoint.event.gen.JobCreated;
import api.bpartners.annotator.model.S3CustomObject;
import api.bpartners.annotator.repository.jpa.model.Task;
import api.bpartners.annotator.service.aws.S3Service;
import java.util.List;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static api.bpartners.annotator.repository.jpa.model.enums.TaskStatus.PENDING;
import static api.bpartners.annotator.service.JobService.toTypedEvent;

@Service
@AllArgsConstructor
@Slf4j
public class JobCreatedService implements Consumer<JobCreated> {
  private final S3Service s3Service;
  private final TaskService taskService;
  private final EventProducer eventProducer;

  @Transactional
  @Override
  public void accept(JobCreated jobCreated) {
    String bucketName = jobCreated.getJob().getBucketName();
    String prefix = jobCreated.getJob().getFolderPath();
    String continuationToken = jobCreated.getNextContinuationToken();
    S3CustomObject response = s3Service.getObjectKeys(bucketName, prefix, continuationToken);
    if (response.getNextContinuationToken() != null) {
      eventProducer.accept(
          List.of(toTypedEvent(jobCreated.getJob(), response.getNextContinuationToken())));
    }
    List<Task> tasksToCreate = response.getObjectsFilename().stream()
        .map(objectKey -> Task.builder()
            .job(jobCreated.getJob())
            .status(PENDING)
            .filename(objectKey)
            .build())
        .toList();
    log.info("{} tasks are to be created from bucket={}", tasksToCreate.size(), bucketName);
    taskService.createTasks(tasksToCreate);
    log.info("{} tasks created.", tasksToCreate.size());
  }
}
