package api.bpartners.annotator.service.event;

import api.bpartners.annotator.endpoint.event.model.AnnotationBatchExportInitiated;
import api.bpartners.annotator.file.FileWriter;
import api.bpartners.annotator.service.AnnotationBatchService;
import api.bpartners.annotator.service.JobExport.ExportService;
import api.bpartners.annotator.service.JobService;
import api.bpartners.annotator.service.utils.ByteWriter;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AnnotationBatchExportInitiatedService
    implements Consumer<AnnotationBatchExportInitiated> {
  public static final String JSON_FILE_EXTENSION = ".json";
  private final ExportService exportService;
  private final JobService jobService;
  private final AnnotationBatchService annotationBatchService;
  private final FileWriter fileWriter;
  private final ByteWriter byteWriter;

  @Override
  public void accept(AnnotationBatchExportInitiated annotationBatchExportInitiated) {
    String jobId = annotationBatchExportInitiated.getJobId();
    var job = jobService.getById(jobId);
    var batches =
        annotationBatchService.findLatestPerTaskByJobIdPaginated(
            jobId,
            annotationBatchExportInitiated.getBeginPage(),
            annotationBatchExportInitiated.getPageSize());
    Object exported =
        exportService.export(job, annotationBatchExportInitiated.getExportFormat(), batches);

    fileWriter.apply(byteWriter.apply(exported), );
  }
}
