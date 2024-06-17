package api.bpartners.annotator.service.JobExport;

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

  public COCO exportCoco(Job job, AnnotationBatch annotationBatch) {
    return cocoExportService.export(job, annotationBatch);
  }

  public COCO exportCoco(Job job, List<AnnotationBatch> annotationBatches) {
    return cocoExportService.export(job, annotationBatches);
  }

  public VGG exportVgg(Job job, AnnotationBatch annotationBatch) {
    return vggExportService.export(job, annotationBatch);
  }

  public VGG exportVgg(Job job, List<AnnotationBatch> annotationBatches) {
    return vggExportService.export(job, annotationBatches);
  }
}
