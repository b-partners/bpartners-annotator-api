package api.bpartners.annotator.service.event;

import static api.bpartners.annotator.repository.model.enums.JobStatus.READY;
import static api.bpartners.annotator.repository.model.enums.TaskStatus.PENDING;
import static api.bpartners.annotator.service.JobService.toEventType;
import static api.bpartners.annotator.service.utils.TemplateResolverUtils.parseTemplateResolver;
import static java.util.UUID.randomUUID;

import api.bpartners.annotator.endpoint.event.EventProducer;
import api.bpartners.annotator.endpoint.event.model.JobCreated;
import api.bpartners.annotator.mail.Email;
import api.bpartners.annotator.mail.Mailer;
import api.bpartners.annotator.model.TokennedCustomS3ObjectList;
import api.bpartners.annotator.repository.model.Job;
import api.bpartners.annotator.repository.model.Task;
import api.bpartners.annotator.service.JobService;
import api.bpartners.annotator.service.TaskService;
import api.bpartners.annotator.service.aws.JobOrTaskS3Service;
import jakarta.mail.internet.InternetAddress;
import java.util.List;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

@Service
@AllArgsConstructor
@Slf4j
public class JobCreatedService implements Consumer<JobCreated> {
  private static final String TASK_CREATION_FINISHED = "task_creation_finished";
  private final JobOrTaskS3Service jobOrTaskS3Service;
  private final TaskService taskService;
  private final EventProducer eventProducer;
  private final JobService jobService;
  private final Mailer mailer;

  @Transactional
  @Override
  public void accept(JobCreated jobCreated) {
    // TODO: test using localstack
    String bucketName = jobCreated.getJob().getBucketName();
    String prefix = jobCreated.getJob().getFolderPath();
    String continuationToken = jobCreated.getNextContinuationToken();
    TokennedCustomS3ObjectList response =
        jobOrTaskS3Service.getObjectKeys(bucketName, prefix, continuationToken);

    List<Task> tasksToCreate =
        response.getObjectsFilename().stream()
            .map(
                objectKey ->
                    Task.builder()
                        .id(randomUUID().toString())
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
      sendEmailFrom(createdJob);
    }
  }

  private Context configureCustomerContext(Job job) {
    Context context = new Context();
    context.setVariable("job", job);
    return context;
  }

  @SneakyThrows
  private void sendEmailFrom(Job job) {
    String subject = "[Bpartners-Annotator] Initialisation de job complète";
    String htmlBody = parseTemplateResolver(TASK_CREATION_FINISHED, configureCustomerContext(job));
    mailer.accept(
        new Email(
            new InternetAddress(job.getOwnerEmail()),
            List.of(),
            List.of(),
            subject,
            htmlBody,
            List.of()));
  }
}
