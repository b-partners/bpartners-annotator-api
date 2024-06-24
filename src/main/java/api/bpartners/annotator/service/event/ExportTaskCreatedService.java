package api.bpartners.annotator.service.event;

import static api.bpartners.annotator.model.exception.ApiException.ExceptionType.SERVER_EXCEPTION;
import static api.bpartners.annotator.service.utils.TemplateResolverUtils.parseTemplateResolver;
import static java.util.UUID.randomUUID;

import api.bpartners.annotator.endpoint.event.model.ExportTaskCreated;
import api.bpartners.annotator.endpoint.rest.model.ExportFormat;
import api.bpartners.annotator.file.FileWriter;
import api.bpartners.annotator.mail.Email;
import api.bpartners.annotator.mail.Mailer;
import api.bpartners.annotator.model.exception.ApiException;
import api.bpartners.annotator.repository.model.ExportTask;
import api.bpartners.annotator.repository.model.Job;
import api.bpartners.annotator.service.ExportTaskService;
import api.bpartners.annotator.service.ExportTaskStatusService;
import api.bpartners.annotator.service.JobExport.ExportService;
import api.bpartners.annotator.service.JobService;
import api.bpartners.annotator.service.utils.ByteWriter;
import jakarta.mail.internet.InternetAddress;
import java.io.File;
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
public class ExportTaskCreatedService implements Consumer<ExportTaskCreated> {
  public static final String JSON_FILE_EXTENSION = ".json";
  private final Mailer mailer;
  private final ExportTaskService exportTaskService;
  private final ExportService exportService;
  private final ExportTaskStatusService exportTaskStatusService;
  private final ByteWriter byteWriter;
  private final FileWriter fileWriter;
  private final JobService jobService;

  @Override
  @Transactional
  public void accept(ExportTaskCreated exportTask) {
    var linkedJob = jobService.getById(exportTask.getJobId());
    var task = exportTaskService.getTaskById(exportTask.getTaskId());
    var emailCC = exportTask.getEmailCC();
    var format = exportTask.getExportFormat();
    exportTaskStatusService.process(task);
    var exported = export(linkedJob, task, format);
    var annotationAsBytes = byteWriter.apply(exported);
    var file =
        fileWriter.write(
            annotationAsBytes, createTempDirectory(), linkedJob.getName() + JSON_FILE_EXTENSION);
    String subject = "[Bpartners-Annotator] Exportation de job sous format " + format;
    String htmlBody = parseTemplateResolver("job_export_finished", configureContext(linkedJob));
    mailer.accept(
        new Email(
            getInternetAddress(linkedJob),
            emailCC == null ? List.of() : List.of(emailCC),
            List.of(),
            subject,
            htmlBody,
            List.of(file)));
  }

  private Object export(Job job, ExportTask task, ExportFormat format) {
    try {
      var exported = exportService.exportJob(job, task.getAnnotationBatches(), format);
      exportTaskStatusService.succeed(task);
      return exported;
    } catch (RuntimeException e) {
      exportTaskStatusService.fail(task);
      throw new ApiException(SERVER_EXCEPTION, e.getMessage());
    }
  }

  @SneakyThrows
  private static InternetAddress getInternetAddress(Job linkedJob) {
    return new InternetAddress(linkedJob.getOwnerEmail());
  }

  @SneakyThrows
  private static File createTempDirectory() {
    return Files.createTempDirectory(randomUUID().toString()).toFile();
  }

  private static Context configureContext(Job job) {
    Context context = new Context();
    context.setVariable("job", job);
    return context;
  }
}
