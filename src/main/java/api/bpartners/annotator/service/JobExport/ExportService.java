package api.bpartners.annotator.service.JobExport;

import api.bpartners.annotator.endpoint.rest.model.ExportFormat;
import api.bpartners.annotator.repository.model.AnnotationBatch;
import api.bpartners.annotator.repository.model.Job;
import api.bpartners.annotator.service.JobExport.model.COCO;
import api.bpartners.annotator.service.JobExport.model.VGG;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ExportService {
  private final CocoExportService cocoExportService;
  private final VggExportService vggExportService;

  public Object export(Job job, ExportFormat format, List<AnnotationBatch> annotationBatches) {
    return switch (format) {
      case COCO -> cocoExportService.export(job, annotationBatches);
      case VGG -> vggExportService.export(job, annotationBatches);
    };
  }
}
