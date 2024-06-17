package api.bpartners.annotator.service.event;

import static java.util.UUID.randomUUID;

import api.bpartners.annotator.endpoint.event.EventProducer;
import api.bpartners.annotator.endpoint.event.model.AnnotationBatchPageExportInitiated;
import api.bpartners.annotator.endpoint.event.model.JobExportInitiated;
import api.bpartners.annotator.endpoint.rest.model.ExportFormat;
import api.bpartners.annotator.repository.model.JobExport;
import api.bpartners.annotator.service.AnnotationBatchService;
import api.bpartners.annotator.service.JobExport.AnnotationBatchPageExportService;
import api.bpartners.annotator.service.JobExport.JobExportService;
import api.bpartners.annotator.service.JobService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JobExportInitiatedService implements Consumer<JobExportInitiated> {
  private final JobService jobService;
  private final AnnotationBatchService annotationBatchService;
  private final EventProducer eventProducer;
  private final int batchSize;
  private final JobExportService jobExportService;
  private final AnnotationBatchPageExportService batchPageExportService;

  public JobExportInitiatedService(
      JobService jobService,
      AnnotationBatchService annotationBatchService,
      EventProducer eventProducer,
      @Value("JOB_ANNOTATION_EXPORT_MAX_BATCH_SIZE") int batchSize,
      JobExportService jobExportService,
      AnnotationBatchPageExportService batchPageExportService) {
    this.jobService = jobService;
    this.annotationBatchService = annotationBatchService;
    this.eventProducer = eventProducer;
    this.batchSize = batchSize;
    this.jobExportService = jobExportService;
    this.batchPageExportService = batchPageExportService;
  }

  @Override
  @Transactional
  public void accept(JobExportInitiated jobExportInitiated) {
    String jobId = jobExportInitiated.getJobId();
    var job = jobService.getById(jobId);
    var savedJobExport = jobExportService.create(job, jobExportInitiated);
    fireAnnotationBatchExportInitiatedEvents(jobExportInitiated, savedJobExport);
  }

  private void fireAnnotationBatchExportInitiatedEvents(
      JobExportInitiated jobExportInitiated, JobExport jobExport) {
    String jobId = jobExport.getJobId();
    int batchCount = annotationBatchService.countLatestBatchPerTaskByJobId(jobId);
    List<AnnotationBatchPageExportInitiated> exportBatchList = new ArrayList<>();
    int numberOfPages = calculatePages(batchCount, batchSize);
    ExportFormat exportFormat = jobExportInitiated.getExportFormat();
    for (int i = 0; i < numberOfPages; i++) {
      exportBatchList.add(
          AnnotationBatchPageExportInitiated.builder()
              .id(randomUUID().toString())
              .jobId(jobId)
              .exportFormat(exportFormat)
              .beginPage(i)
              .pageSize(batchCount)
              .build());
    }
    var savedBatches =
        exportBatchList.stream()
            .map(exportBatch -> batchPageExportService.create(exportBatch, jobExport));
    eventProducer.accept(Arrays.asList(exportBatchList.toArray()));
  }

  private static int calculatePages(int nbOfElements, int batchSize) {
    int fullPages = nbOfElements / batchSize;
    int remainder = nbOfElements % batchSize;
    return fullPages + (remainder != 0 ? 1 : 0);
  }
}
