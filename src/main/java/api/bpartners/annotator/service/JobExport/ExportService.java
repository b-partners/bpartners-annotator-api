package api.bpartners.annotator.service.JobExport;

import static api.bpartners.annotator.endpoint.rest.model.ExportFormat.COCO;
import static api.bpartners.annotator.endpoint.rest.model.ExportFormat.VGG;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;

import api.bpartners.annotator.endpoint.event.EventProducer;
import api.bpartners.annotator.endpoint.event.gen.JobExportInitiated;
import api.bpartners.annotator.endpoint.rest.model.ExportFormat;
import api.bpartners.annotator.model.exception.BadRequestException;
import api.bpartners.annotator.repository.model.AnnotationBatch;
import api.bpartners.annotator.repository.model.Job;
import api.bpartners.annotator.repository.model.Task;
import api.bpartners.annotator.service.AnnotationBatchService;
import api.bpartners.annotator.service.JobService;
import api.bpartners.annotator.service.TaskService;
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
  private final AnnotationBatchService annotationBatchService;
  private final TaskService taskService;

  public void initiateJobExport(String jobId, ExportFormat exportFormat) {
    var linkedJob = jobService.getById(jobId);
    eventProducer.accept(List.of(new JobExportInitiated(linkedJob, exportFormat)));
  }

  @Transactional(propagation = REQUIRED, readOnly = true, rollbackFor = Exception.class)
  public Object exportJob(Job job, ExportFormat format) {
    if (VGG.equals(format)) {
      var batches = findAnnotationBatchesWithUpdatedTasks(job.getId());
      return vggExportService.export(job, batches);
    }
    if (COCO.equals(format)) {
      var batches = findAnnotationBatchesWithUpdatedTasks(job.getId());
      return cocoExportService.export(job, batches);
    }
    throw new BadRequestException("unknown export format " + format);
  }

  List<AnnotationBatch> findAnnotationBatchesWithUpdatedTasks(String jobId) {
    return annotationBatchService.findLatestPerTaskByJobId(jobId).stream()
        .peek(
            batch -> {
              Task linkedTask = batch.getTask();
              if (linkedTask.hasMissingFileInfoFields()) {
                batch.setTask(taskService.refreshFileInfos(linkedTask));
              }
            })
        .toList();
  }
}
