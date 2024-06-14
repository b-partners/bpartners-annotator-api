package api.bpartners.annotator.service.JobExport;

import static java.util.UUID.randomUUID;

import api.bpartners.annotator.endpoint.event.EventProducer;
import api.bpartners.annotator.endpoint.event.model.JobExportInitiated;
import api.bpartners.annotator.endpoint.rest.model.ExportFormat;
import jakarta.mail.internet.InternetAddress;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ExportService {
  private final EventProducer eventProducer;

  public void initiateJobExport(String jobId, ExportFormat exportFormat, InternetAddress emailCC) {
    eventProducer.accept(
        List.of(new JobExportInitiated(randomUUID().toString(), jobId, exportFormat, emailCC)));
  }
}
