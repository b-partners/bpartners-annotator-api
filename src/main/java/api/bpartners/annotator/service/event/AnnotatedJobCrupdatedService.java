package api.bpartners.annotator.service.event;

import static api.bpartners.annotator.repository.model.enums.JobStatus.READY;
import static api.bpartners.annotator.repository.model.enums.TaskStatus.TO_CORRECT;
import static api.bpartners.annotator.service.utils.TemplateResolverUtils.parseTemplateResolver;
import static java.util.Objects.requireNonNull;

import api.bpartners.annotator.endpoint.event.gen.AnnotatedJobCrupdated;
import api.bpartners.annotator.endpoint.rest.controller.mapper.AnnotationBatchMapper;
import api.bpartners.annotator.endpoint.rest.model.AnnotatedTask;
import api.bpartners.annotator.endpoint.rest.model.CrupdateAnnotatedJob;
import api.bpartners.annotator.mail.Email;
import api.bpartners.annotator.mail.Mailer;
import api.bpartners.annotator.repository.model.AnnotationBatch;
import api.bpartners.annotator.repository.model.Job;
import api.bpartners.annotator.repository.model.Task;
import api.bpartners.annotator.service.AnnotationBatchService;
import api.bpartners.annotator.service.JobService;
import api.bpartners.annotator.service.TaskService;
import jakarta.mail.internet.InternetAddress;
import java.util.List;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

@Service
@AllArgsConstructor
public class AnnotatedJobCrupdatedService implements Consumer<AnnotatedJobCrupdated> {
  private static final String ANNOTATED_JOB_TASK_AND_ANNOTATIONS_CREATION_FINISHED =
      "annotated_job_task_and_annotations_creation_finished";
  private final TaskService taskService;
  private final AnnotationBatchService annotationBatchService;
  private final AnnotationBatchMapper annotationBatchRestMapper;
  private final JobService jobService;
  private final Mailer mailer;

  @Override
  @Transactional
  public void accept(AnnotatedJobCrupdated annotatedJobCrupdated) {
    CrupdateAnnotatedJob crupdateAnnotatedJob = annotatedJobCrupdated.getCrupdateAnnotatedJob();
    Job linkedJob = annotatedJobCrupdated.getJob();

    requireNonNull(crupdateAnnotatedJob.getAnnotatedTasks())
        .forEach(annotatedTask -> saveAndAnnotateTask(annotatedTask, linkedJob));

    Job refreshedJob = jobService.getById(linkedJob.getId());
    refreshedJob.setStatus(READY);
    jobService.save(refreshedJob);
    sendEmailFrom(refreshedJob);
  }

  private void saveAndAnnotateTask(AnnotatedTask annotatedTask, Job savedJob) {
    Task linkedTask =
        taskService.save(
            Task.builder()
                .id(annotatedTask.getId())
                .filename(annotatedTask.getFilename())
                .job(savedJob)
                .userId(annotatedTask.getAnnotatorId())
                .status(TO_CORRECT)
                .build());
    AnnotationBatch linkedBatch =
        annotationBatchRestMapper.toDomain(
            annotatedTask.getAnnotatorId(),
            linkedTask.getId(),
            requireNonNull(annotatedTask.getAnnotationBatch()));
    annotationBatchService.annotateTask(linkedBatch);
  }

  private Context configureCustomerContext(Job job) {
    Context context = new Context();
    context.setVariable("job", job);
    return context;
  }

  @SneakyThrows
  private void sendEmailFrom(Job job) {
    String subject = "[Bpartners-Annotator] Initialisation de job complète";
    String htmlBody =
        parseTemplateResolver(
            ANNOTATED_JOB_TASK_AND_ANNOTATIONS_CREATION_FINISHED, configureCustomerContext(job));
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
