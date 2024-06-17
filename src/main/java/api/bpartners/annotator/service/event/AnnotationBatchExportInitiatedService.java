package api.bpartners.annotator.service.event;

import static java.util.UUID.randomUUID;

import api.bpartners.annotator.endpoint.event.model.AnnotationBatchExportInitiated;
import api.bpartners.annotator.file.BucketComponent;
import api.bpartners.annotator.file.FileWriter;
import api.bpartners.annotator.service.AnnotationBatchService;
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
public class AnnotationBatchExportInitiatedService
    implements Consumer<AnnotationBatchExportInitiated> {
  public static final String JSON_FILE_EXTENSION = ".json";
  private final ExportService exportService;
  private final JobService jobService;
  private final AnnotationBatchService annotationBatchService;
  private final FileWriter fileWriter;
  private final ByteWriter byteWriter;
  private final BucketComponent bucketComponent;

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
    var exportedAsBytes = byteWriter.apply(exported);
    var inFile =
        fileWriter.write(
            exportedAsBytes, createTempDirectory(), job.getName() + JSON_FILE_EXTENSION);
    String bucketKey = getBucketKey("", annotationBatchExportInitiated);
    bucketComponent.upload(inFile, bucketKey);
    log.info("successfully exported {} to {}", annotationBatchExportInitiated.getId(), bucketKey);
  }

  @SneakyThrows
  private static File createTempDirectory() {
    return Files.createTempDirectory(randomUUID().toString()).toFile();
  }

  private static String getBucketKey(
      String jobExportFolderPath, AnnotationBatchExportInitiated annotationBatchExportInitiated) {
    return jobExportFolderPath + annotationBatchExportInitiated.getId() + JSON_FILE_EXTENSION;
  }
}
