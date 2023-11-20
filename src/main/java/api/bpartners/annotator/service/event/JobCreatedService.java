package api.bpartners.annotator.service.event;

import static api.bpartners.annotator.repository.model.enums.JobStatus.READY;
import static api.bpartners.annotator.repository.model.enums.TaskStatus.PENDING;
import static api.bpartners.annotator.service.JobService.toEventType;
import static api.bpartners.annotator.service.utils.TemplateResolverUtils.parseTemplateResolver;

import api.bpartners.annotator.endpoint.event.EventProducer;
import api.bpartners.annotator.endpoint.event.gen.JobCreated;
import api.bpartners.annotator.model.S3CustomObject;
import api.bpartners.annotator.repository.model.Job;
import api.bpartners.annotator.repository.model.Task;
import api.bpartners.annotator.service.JobService;
import api.bpartners.annotator.service.TaskService;
import api.bpartners.annotator.service.aws.S3Service;
import api.bpartners.annotator.service.aws.SesService;
import java.util.List;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

@Service
@AllArgsConstructor
@Slf4j
public class JobCreatedService implements Consumer<JobCreated> {
  private static final String TASK_CREATION_FINISHED = "task_creation_finished";
  private final S3Service s3Service;
  private final TaskService taskService;
  private final EventProducer eventProducer;
  private final JobService jobService;
  private final SesService sesService;

  @Transactional
  @Override
  public void accept(JobCreated jobCreated) {
    String bucketName = jobCreated.getJob().getBucketName();
    String prefix = jobCreated.getJob().getFolderPath();
    String continuationToken = jobCreated.getNextContinuationToken();
    S3CustomObject response = s3Service.getObjectKeys(bucketName, prefix, continuationToken);

    List<Task> tasksToCreate =
        response.getObjectsFilename().stream()
            .map(
                objectKey ->
                    Task.builder()
                        .job(jobCreated.getJob())
                        .status(PENDING)
                        .filename(objectKey)
                        .build())
            .toList();
    log.info("{} tasks are to be created from bucket={}", tasksToCreate.size(), bucketName);
    taskService.createTasks(tasksToCreate);
    log.info("{} tasks created.", tasksToCreate.size());

    if (response.getNextContinuationToken() != null) {
      eventProducer.accept(
          List.of(toEventType(jobCreated.getJob(), response.getNextContinuationToken())));
    } else {
      Job createdJob = jobService.getById(jobCreated.getJob().getId());
      createdJob.setStatus(READY);
      jobService.save(createdJob);
      String subject = "[Bpartners-Annotator] Initialisation de job complète";
      String htmlBody =
          parseTemplateResolver(TASK_CREATION_FINISHED, configureCustomerContext(createdJob));
      sesService.sendEmail(createdJob.getOwnerEmail(), subject, htmlBody);
    }
  }

  private Context configureCustomerContext(Job job) {
    Context context = new Context();
    context.setVariable("job", job);
    return context;
  }
}
