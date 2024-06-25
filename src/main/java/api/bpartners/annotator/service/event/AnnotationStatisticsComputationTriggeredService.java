package api.bpartners.annotator.service.event;

import static api.bpartners.annotator.service.event.ExportTaskCreatedService.JSON_FILE_EXTENSION;
import static api.bpartners.annotator.service.utils.TemplateResolverUtils.parseTemplateResolver;
import static java.util.UUID.randomUUID;

import api.bpartners.annotator.endpoint.event.model.AnnotationStatisticsComputationTriggered;
import api.bpartners.annotator.endpoint.rest.model.AnnotationNumberPerLabel;
import api.bpartners.annotator.mail.Email;
import api.bpartners.annotator.mail.Mailer;
import api.bpartners.annotator.repository.model.Job;
import api.bpartners.annotator.service.AnnotationBatchService;
import api.bpartners.annotator.service.JobService;
import api.bpartners.annotator.service.utils.ByteWriter;
import jakarta.mail.internet.InternetAddress;
import java.nio.file.Files;
import java.util.List;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

@Service
@AllArgsConstructor
public class AnnotationStatisticsComputationTriggeredService
    implements Consumer<AnnotationStatisticsComputationTriggered> {
  private final AnnotationBatchService annotationBatchService;
  private final JobService jobService;
  private final Mailer mailer;
  private final ByteWriter writer;

  @SneakyThrows
  @Override
  @Transactional
  public void accept(
      AnnotationStatisticsComputationTriggered annotationStatisticsComputationTriggered) {
    Job linkedJob = jobService.getById(annotationStatisticsComputationTriggered.getJobId());
    InternetAddress cc = annotationStatisticsComputationTriggered.getEmailCC();
    String subject = "[Bpartners-Annotator] Calcul de statistiques de job";
    String htmlBody =
        parseTemplateResolver("job_statistics_computation_finished", configureContext(linkedJob));

    List<AnnotationNumberPerLabel> latestAnnotationStatistics =
        annotationBatchService.getLatestAnnotationStatistics(linkedJob);
    var statisticsAsBytes = writer.apply(latestAnnotationStatistics);
    var inFile =
        writer.writeAsFile(
            statisticsAsBytes,
            Files.createTempDirectory(randomUUID().toString()).toFile(),
            linkedJob.getName() + "_statistics_" + JSON_FILE_EXTENSION);
    mailer.accept(
        new Email(
            new InternetAddress(linkedJob.getOwnerEmail()),
            cc == null ? List.of() : List.of(cc),
            List.of(),
            subject,
            htmlBody,
            List.of(inFile)));
  }

  private Context configureContext(Job job) {
    Context context = new Context();
    context.setVariable("job", job);
    return context;
  }
}
