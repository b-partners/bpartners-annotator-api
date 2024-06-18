package api.bpartners.annotator.service.JobExport;

import static org.springframework.transaction.annotation.Propagation.REQUIRED;

import api.bpartners.annotator.endpoint.event.EventProducer;
import api.bpartners.annotator.endpoint.event.model.JobExportInitiated;
import api.bpartners.annotator.endpoint.rest.model.ExportFormat;
import api.bpartners.annotator.model.exception.BadRequestException;
import api.bpartners.annotator.repository.model.AnnotationBatch;
import api.bpartners.annotator.repository.model.Job;
import api.bpartners.annotator.service.AnnotationBatchService;
import api.bpartners.annotator.service.JobService;
import jakarta.mail.internet.InternetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
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

  @SneakyThrows
  public void initiateJobExport(String jobId, ExportFormat exportFormat, String emailCC) {
    eventProducer.accept(
        List.of(new JobExportInitiated(jobId, exportFormat, new InternetAddress(emailCC))));
  }

  @Transactional(propagation = REQUIRED, readOnly = true, rollbackFor = Exception.class)
  public List<Object> exportJob(Job job, ExportFormat format) {
    var batches = annotationBatchService.findLatestPerTaskByJobId(job.getId());
    var subBatches = partition(batches);
    return switch (format) {
      case VGG -> subBatches.parallelStream()
          .filter(batch -> !batch.isEmpty())
          .map(batch -> vggExportService.export(job, batch))
          .collect(Collectors.toUnmodifiableList());
      case COCO -> subBatches.parallelStream()
          .filter(batch -> !batch.isEmpty())
          .map(batch -> cocoExportService.export(job, batch))
          .collect(Collectors.toUnmodifiableList());
      case null -> throw new BadRequestException("unknown export format " + format);
    };
  }

  private List<List<AnnotationBatch>> partition(List<AnnotationBatch> batches) {
    int middleIndex = batches.size() / 2;
    Map<Boolean, List<AnnotationBatch>> groups =
        batches.stream().collect(Collectors.partitioningBy(s -> batches.indexOf(s) >= middleIndex));
    return new ArrayList<>(groups.values());
  }
}
