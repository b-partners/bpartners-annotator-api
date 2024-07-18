package api.bpartners.annotator.service.event;

import static api.bpartners.annotator.service.utils.TemplateResolverUtils.parseTemplateResolver;
import static java.util.UUID.randomUUID;

import api.bpartners.annotator.endpoint.event.model.ExportTaskCreated;
import api.bpartners.annotator.mail.Email;
import api.bpartners.annotator.mail.Mailer;
import api.bpartners.annotator.repository.model.Job;
import api.bpartners.annotator.service.JobExport.ExportService;
import api.bpartners.annotator.service.JobService;
import api.bpartners.annotator.service.utils.ByteWriter;
import jakarta.mail.internet.InternetAddress;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;
import java.util.Set;
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
  private final ExportService exportService;
  private final ByteWriter writer;
  private final JobService jobService;

  @Override
  @Transactional
  public void accept(ExportTaskCreated exportTask) {
    var linkedJob = jobService.getById(exportTask.getJobId());
    var emailCC = exportTask.getEmailCC();
    var format = exportTask.getExportFormat();
    var exported = exportService.export(linkedJob, exportTask.getTaskId(), format);
    var annotationAsBytes = writer.apply(exported);
    var file =
        writer.writeAsFile(
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

  @SneakyThrows
  private static InternetAddress getInternetAddress(Job linkedJob) {
    return new InternetAddress(linkedJob.getOwnerEmail());
  }

  @SneakyThrows
  private static File createTempDirectory() {
    Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwx------");
    return Files.createTempDirectory(
            randomUUID().toString(), PosixFilePermissions.asFileAttribute(perms))
        .toFile();
  }

  private static Context configureContext(Job job) {
    Context context = new Context();
    context.setVariable("job", job);
    return context;
  }
}
