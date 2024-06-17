package api.bpartners.annotator.service.JobExport;

import static api.bpartners.annotator.service.event.AnnotationBatchPageExportInitiatedService.JSON_FILE_EXTENSION;

import api.bpartners.annotator.endpoint.event.model.AnnotationBatchPageExportInitiated;
import api.bpartners.annotator.model.exception.NotFoundException;
import api.bpartners.annotator.repository.jpa.AnnotationBatchPageExportRepository;
import api.bpartners.annotator.repository.model.AnnotationBatchPageExport;
import api.bpartners.annotator.repository.model.JobExport;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AnnotationBatchPageExportService {
  private final AnnotationBatchPageExportRepository repository;
  private final AnnotationBatchPageExportStatusService statusService;

  public final AnnotationBatchPageExport create(
      final AnnotationBatchPageExportInitiated annotationBatchPageExportInitiated,
      final JobExport jobExport) {
    var toSave =
        AnnotationBatchPageExport.builder()
            .id(annotationBatchPageExportInitiated.getId())
            // no need to set status as default Statusable::getStatus will return pending unknown on
            // empty
            .statuses(List.of())
            .bucketKey(getBucketKey(jobExport.getFolderPath(), annotationBatchPageExportInitiated))
            .exportFormat(jobExport.getExportFormat())
            .beginPage(annotationBatchPageExportInitiated.getBeginPage())
            .pageSize(annotationBatchPageExportInitiated.getPageSize())
            .build();
    return repository.save(toSave);
  }

  public final AnnotationBatchPageExport findById(String id) {
    return repository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("JobExport.Id = " + id + " not found."));
  }

  public final AnnotationBatchPageExport complete(String id) {
    var persisted = findById(id);
    statusService.succeed(persisted);
    return persisted;
  }

  private static String getBucketKey(
      String jobExportFolderPath,
      AnnotationBatchPageExportInitiated annotationBatchPageExportInitiated) {
    return jobExportFolderPath + annotationBatchPageExportInitiated.getId() + JSON_FILE_EXTENSION;
  }
}
