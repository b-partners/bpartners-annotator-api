package api.bpartners.annotator.service;

import api.bpartners.annotator.endpoint.event.gen.JobCreated;
import api.bpartners.annotator.repository.jpa.model.Task;
import api.bpartners.annotator.service.aws.S3Service;
import java.util.List;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static api.bpartners.annotator.repository.jpa.model.enums.TaskStatus.PENDING;

@Service
@AllArgsConstructor
@Slf4j
public class JobCreatedService implements Consumer<JobCreated> {
  private final S3Service s3Service;
  private final TaskService taskService;

  @Transactional
  @Override
  public void accept(JobCreated jobCreated) {
    String bucketName = jobCreated.getJob().getBucketName();
    String prefix = jobCreated.getJob().getFolderPath();
    String continuationToken = jobCreated.getContinuationToken();
    List<String> objectKeys = s3Service.getObjectKeys(bucketName, prefix, continuationToken);
    List<Task> tasksToCreate = objectKeys.stream()
        .map(objectKey -> Task.builder()
            .job(jobCreated.getJob())
            .status(PENDING)
            .s3ImageKey(objectKey)
            .build())
        .toList();
    log.info("{} tasks are to be created from bucket={}", tasksToCreate.size(), bucketName);
    taskService.createTasks(tasksToCreate);
    log.info("{} tasks created.", tasksToCreate.size());
  }
}
