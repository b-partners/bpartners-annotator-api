package api.bpartners.annotator.service.event;

import api.bpartners.annotator.endpoint.event.model.AnnotationBatchExportInitiated;
import api.bpartners.annotator.service.JobExport.ExportService;
import api.bpartners.annotator.service.JobService;
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

  @Override
  public void accept(AnnotationBatchExportInitiated annotationBatchExportInitiated) {}
}
