package api.bpartners.annotator.service.JobExport;

import static api.bpartners.annotator.model.exception.ApiException.ExceptionType.SERVER_EXCEPTION;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;

import api.bpartners.annotator.endpoint.event.EventProducer;
import api.bpartners.annotator.endpoint.event.model.JobExportInitiated;
import api.bpartners.annotator.endpoint.rest.model.ExportFormat;
import api.bpartners.annotator.model.exception.ApiException;
import api.bpartners.annotator.model.exception.BadRequestException;
import api.bpartners.annotator.repository.model.AnnotationBatch;
import api.bpartners.annotator.repository.model.Job;
import api.bpartners.annotator.service.ExportTaskService;
import api.bpartners.annotator.service.ExportTaskStatusService;
import jakarta.mail.internet.InternetAddress;
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
  private final ExportTaskService exportTaskService;
  private final ExportTaskStatusService exportTaskStatusService;

  @SneakyThrows
  public void initiateJobExport(String jobId, ExportFormat exportFormat, String emailCC) {
    eventProducer.accept(
        List.of(new JobExportInitiated(jobId, exportFormat, new InternetAddress(emailCC))));
  }

  @Transactional(propagation = REQUIRED, readOnly = true, rollbackFor = Exception.class)
  private Object exportJob(Job job, List<AnnotationBatch> batches, ExportFormat format) {
    return switch (format) {
      case VGG -> vggExportService.export(job, batches);
      case COCO -> cocoExportService.export(job, batches);
      default -> throw new BadRequestException("unknown export format " + format);
    };
  }

  public Object export(Job job, String taskId, ExportFormat format) {
    var task = exportTaskService.getTaskById(taskId);
    exportTaskStatusService.process(task);
    Object exported;
    try {
      exported = exportJob(job, task.getAnnotationBatches(), format);
    } catch (RuntimeException e) {
      exportTaskStatusService.fail(task);
      throw new ApiException(SERVER_EXCEPTION, e.getMessage());
    }
    exportTaskStatusService.succeed(task);
    return exported;
  }
}
