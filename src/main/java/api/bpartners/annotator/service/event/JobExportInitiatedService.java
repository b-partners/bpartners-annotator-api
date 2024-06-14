package api.bpartners.annotator.service.event;

import static java.util.UUID.randomUUID;

import api.bpartners.annotator.endpoint.event.EventProducer;
import api.bpartners.annotator.endpoint.event.model.AnnotationBatchExportInitiated;
import api.bpartners.annotator.endpoint.event.model.JobExportInitiated;
import api.bpartners.annotator.endpoint.rest.model.ExportFormat;
import api.bpartners.annotator.repository.model.Job;
import api.bpartners.annotator.service.AnnotationBatchService;
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

  public JobExportInitiatedService(
      JobService jobService,
      AnnotationBatchService annotationBatchService,
      EventProducer eventProducer,
      @Value("JOB_ANNOTATION_EXPORT_MAX_BATCH_SIZE") int batchSize) {
    this.jobService = jobService;
    this.annotationBatchService = annotationBatchService;
    this.eventProducer = eventProducer;
    this.batchSize = batchSize;
  }

  @Override
  @Transactional
  public void accept(JobExportInitiated jobExportInitiated) {
    String jobId = jobExportInitiated.getJobId();
    var job = jobService.getById(jobId);
    var folderPath = createAnnotationBatchExportFolderPathFrom(job);
    //save jobExportInitiatedInfo
    fireAnnotationBatchExportInitiatedEvents(jobExportInitiated, jobId);
  }

  private void fireAnnotationBatchExportInitiatedEvents(
      JobExportInitiated jobExportInitiated, String jobId) {
    int batchCount = annotationBatchService.countLatestBatchPerTaskByJobId(jobId);
    List<AnnotationBatchExportInitiated> exportBatchList = new ArrayList<>();
    int numberOfPages = calculatePages(batchCount, batchSize);
    ExportFormat exportFormat = jobExportInitiated.getExportFormat();
    for (int i = 0; i < numberOfPages; i++) {
      exportBatchList.add(
          AnnotationBatchExportInitiated.builder()
              .jobId(jobId)
              .beginPage(i)
              .exportFormat(exportFormat)
              .pageSize(batchCount)
              .build());
    }
    // TODO: waiting for eventProducer to accept List<PojaEvent>
    eventProducer.accept(Arrays.asList(exportBatchList.toArray()));
  }

  private static String createAnnotationBatchExportFolderPathFrom(Job job) {
    return job.getName().endsWith("/")
        ? (job.getName())
        : (job.getName() + "/") + randomUUID() + "/";
  }

  private static int calculatePages(int nbOfElements, int batchSize) {
    int fullPages = nbOfElements / batchSize;
    int remainder = nbOfElements % batchSize;
    return fullPages + (remainder != 0 ? 1 : 0);
  }
}
