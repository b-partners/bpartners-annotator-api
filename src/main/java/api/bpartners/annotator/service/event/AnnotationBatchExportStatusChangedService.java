package api.bpartners.annotator.service.event;

import static java.util.UUID.randomUUID;

import api.bpartners.annotator.endpoint.event.model.AnnotationBatchPageExportInitiated;
import api.bpartners.annotator.repository.model.Job;
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
public class AnnotationBatchExportStatusChangedService
    implements Consumer<AnnotationBatchPageExportInitiated> {

  @Override
  @Transactional
  public void accept(AnnotationBatchPageExportInitiated annotationBatchPageExportInitiated) {}

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
