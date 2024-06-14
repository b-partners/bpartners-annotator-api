package api.bpartners.annotator.service.event;

import static java.util.UUID.randomUUID;

import api.bpartners.annotator.endpoint.event.model.AnnotationBatchExportInitiated;
import api.bpartners.annotator.file.FileWriter;
import api.bpartners.annotator.repository.model.Job;
import api.bpartners.annotator.service.JobExport.ExportService;
import api.bpartners.annotator.service.JobService;
import api.bpartners.annotator.service.utils.ByteWriter;
import jakarta.mail.internet.InternetAddress;
import java.io.File;
import java.nio.file.Files;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

@Service
@AllArgsConstructor
public class AnnotationBatchExportInitiatedService
    implements Consumer<AnnotationBatchExportInitiated> {
  public static final String JSON_FILE_EXTENSION = ".json";
  private final ExportService exportService;
  private final ByteWriter byteWriter;
  private final FileWriter fileWriter;
  private final JobService jobService;

  @Override
  @Transactional
  public void accept(AnnotationBatchExportInitiated annotationBatchExportInitiated) {}

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
