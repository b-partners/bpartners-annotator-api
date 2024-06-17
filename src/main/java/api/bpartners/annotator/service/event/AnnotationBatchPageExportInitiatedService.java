package api.bpartners.annotator.service.event;

import static java.util.UUID.randomUUID;

import api.bpartners.annotator.endpoint.event.model.AnnotationBatchPageExportInitiated;
import api.bpartners.annotator.file.BucketComponent;
import api.bpartners.annotator.file.FileWriter;
import api.bpartners.annotator.service.AnnotationBatchService;
import api.bpartners.annotator.service.JobExport.AnnotationBatchPageExportService;
import api.bpartners.annotator.service.JobExport.ExportService;
import api.bpartners.annotator.service.JobService;
import api.bpartners.annotator.service.utils.ByteWriter;
import java.io.File;
import java.nio.file.Files;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class AnnotationBatchPageExportInitiatedService
    implements Consumer<AnnotationBatchPageExportInitiated> {
  public static final String JSON_FILE_EXTENSION = ".json";
  private final ExportService exportService;
  private final JobService jobService;
  private final AnnotationBatchService annotationBatchService;
  private final AnnotationBatchPageExportService annotationBatchPageExportService;
  private final FileWriter fileWriter;
  private final ByteWriter byteWriter;
  private final BucketComponent bucketComponent;

  @Override
  public void accept(AnnotationBatchPageExportInitiated annotationBatchPageExportInitiated) {
    String jobId = annotationBatchPageExportInitiated.getJobId();
    var job = jobService.getById(jobId);
    String id = annotationBatchPageExportInitiated.getId();
    var annotationBatchPageExport = annotationBatchPageExportService.findById(id);
    String bucketKey = annotationBatchPageExport.getBucketKey();
    var batches =
        annotationBatchService.findLatestPerTaskByJobIdPaginated(
            jobId,
            annotationBatchPageExportInitiated.getBeginPage(),
            annotationBatchPageExportInitiated.getPageSize());
    Object exported =
        exportService.export(job, annotationBatchPageExportInitiated.getExportFormat(), batches);
    var exportedAsBytes = byteWriter.apply(exported);
    var inFile = fileWriter.write(exportedAsBytes, createTempDirectory(), bucketKey);
    bucketComponent.upload(inFile, bucketKey);

    var completed = annotationBatchPageExportService.complete(id);
    log.info("successfully exported {} to {}", id, bucketKey);
  }

  @SneakyThrows
  private static File createTempDirectory() {
    return Files.createTempDirectory(randomUUID().toString()).toFile();
  }
}
