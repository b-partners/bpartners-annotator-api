package api.bpartners.annotator.service.event;

import static api.bpartners.annotator.service.utils.TemplateResolverUtils.parseTemplateResolver;
import static java.util.UUID.randomUUID;

import api.bpartners.annotator.endpoint.event.gen.JobExportInitiated;
import api.bpartners.annotator.endpoint.rest.model.ExportFormat;
import api.bpartners.annotator.file.FileWriter;
import api.bpartners.annotator.mail.Email;
import api.bpartners.annotator.mail.Mailer;
import api.bpartners.annotator.repository.model.Job;
import api.bpartners.annotator.service.JobExport.ExportService;
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
public class JobExportInitiatedService implements Consumer<JobExportInitiated> {
  public static final String JSON_FILE_EXTENSION = ".json";
  private final Mailer mailer;
  private final ExportService exportService;
  private final ByteWriter byteWriter;
  private final FileWriter fileWriter;

  @SneakyThrows
  @Override
  @Transactional
  public void accept(JobExportInitiated jobExportInitiated) {
    Job linkedJob = jobExportInitiated.getJob();
    ExportFormat exportFormat = jobExportInitiated.getExportFormat();
    InternetAddress cc = jobExportInitiated.getEmailCC();
    var exported = exportService.exportJob(linkedJob, exportFormat);
    var exportedAsBytes = byteWriter.apply(exported);
    var inFile =
        fileWriter.write(
            exportedAsBytes,
            Files.createTempDirectory(randomUUID().toString()).toFile(),
            linkedJob.getName() + JSON_FILE_EXTENSION);
    String subject = "[Bpartners-Annotator] Exportation de job sous format " + exportFormat;
    String htmlBody = parseTemplateResolver("job_export_finished", configureContext(linkedJob));

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
