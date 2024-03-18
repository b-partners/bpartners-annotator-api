package api.bpartners.annotator.service.JobExport;

import static api.bpartners.annotator.endpoint.rest.model.ExportFormat.COCO;
import static api.bpartners.annotator.endpoint.rest.model.ExportFormat.VGG;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;

import api.bpartners.annotator.endpoint.event.EventProducer;
import api.bpartners.annotator.endpoint.event.gen.JobExportInitiated;
import api.bpartners.annotator.endpoint.rest.model.ExportFormat;
import api.bpartners.annotator.model.exception.BadRequestException;
import api.bpartners.annotator.repository.model.Job;
import api.bpartners.annotator.service.JobService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ExportService {
  private final EventProducer eventProducer;
  private final JobService jobService;
  private final VggExportService vggExportService;
  private final CocoExportService cocoExportService;

  public void initiateJobExport(String jobId, ExportFormat exportFormat) {
    var linkedJob = jobService.getById(jobId);
    eventProducer.accept(List.of(new JobExportInitiated(linkedJob, exportFormat)));
  }

  @Transactional(propagation = REQUIRED, readOnly = true, rollbackFor = Exception.class)
  public Object exportJob(Job job, ExportFormat format) {
    if (VGG.equals(format)) {
      return vggExportService.export(job);
    }
    if (COCO.equals(format)) {
      return cocoExportService.export(job);
    }
    throw new BadRequestException("unknown export format " + format);
  }
}
