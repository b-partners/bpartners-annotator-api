package api.bpartners.annotator.service.JobExport;

import static org.springframework.transaction.annotation.Propagation.REQUIRED;

import api.bpartners.annotator.endpoint.event.EventProducer;
import api.bpartners.annotator.endpoint.event.model.JobExportInitiated;
import api.bpartners.annotator.endpoint.rest.controller.mapper.EmailAddressMapper;
import api.bpartners.annotator.endpoint.rest.model.ExportFormat;
import api.bpartners.annotator.model.exception.BadRequestException;
import api.bpartners.annotator.repository.model.AnnotationBatch;
import api.bpartners.annotator.repository.model.Job;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ExportService {
  private final EventProducer eventProducer;
  private final VggExportService vggExportService;
  private final CocoExportService cocoExportService;

  @SneakyThrows
  public void initiateJobExport(String jobId, ExportFormat exportFormat, String emailCC) {
    eventProducer.accept(
        List.of(new JobExportInitiated(jobId, exportFormat, EmailAddressMapper.from(emailCC))));
  }

  @Transactional(propagation = REQUIRED, readOnly = true, rollbackFor = Exception.class)
  public Object exportJob(Job job, ExportFormat format, List<AnnotationBatch> batches) {
    return switch (format) {
      case VGG -> vggExportService.export(job, batches);
      case COCO -> cocoExportService.export(job, batches);
      case null -> throw new BadRequestException("unknown export format " + format);
    };
  }
}
