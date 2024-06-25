package api.bpartners.annotator.service.event;

import static java.util.UUID.randomUUID;

import api.bpartners.annotator.endpoint.event.EventProducer;
import api.bpartners.annotator.endpoint.event.model.ExportTaskCreated;
import api.bpartners.annotator.endpoint.event.model.JobExportInitiated;
import api.bpartners.annotator.endpoint.rest.model.ExportFormat;
import api.bpartners.annotator.repository.model.AnnotationBatch;
import api.bpartners.annotator.repository.model.ExportTask;
import api.bpartners.annotator.service.AnnotationBatchService;
import api.bpartners.annotator.service.ExportTaskService;
import com.google.common.collect.Lists;
import jakarta.mail.internet.InternetAddress;
import java.util.List;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class JobExportInitiatedService implements Consumer<JobExportInitiated> {
  // TODO: put as env vars
  private static final int BATCH_PARTITION_SIZE = 1000;
  private static final int MAX_HANDLED_BATCH_SIZE = 1200;
  private final AnnotationBatchService annotationBatchService;
  private final ExportTaskService exportTaskService;
  private final EventProducer eventProducer;

  @Override
  @Transactional
  public void accept(JobExportInitiated jobExportInitiated) {
    String jobId = jobExportInitiated.getJobId();
    ExportFormat format = jobExportInitiated.getExportFormat();
    InternetAddress emailCC = jobExportInitiated.getEmailCC();
    var batches = annotationBatchService.findLatestPerTaskByJobId(jobId);
    List<List<AnnotationBatch>> subBatches = split(batches);
    var saved = createTasks(jobId, subBatches);

    saved.forEach(
        task ->
            eventProducer.accept(
                List.of(new ExportTaskCreated(jobId, task.getId(), format, emailCC))));
  }

  private List<List<AnnotationBatch>> split(List<AnnotationBatch> batches) {
    if (batches.size() < MAX_HANDLED_BATCH_SIZE) {
      return List.of(batches);
    }
    return Lists.partition(batches, BATCH_PARTITION_SIZE);
  }

  private List<ExportTask> createTasks(String jobId, List<List<AnnotationBatch>> subBatches) {
    var tasks = subBatches.stream().map(subBatch -> createExportTask(jobId, subBatch)).toList();
    return exportTaskService.saveAll(tasks);
  }

  private ExportTask createExportTask(String jobId, List<AnnotationBatch> subBatch) {
    String taskId = randomUUID().toString();
    var toSave =
        subBatch.stream().peek(annotationBatch -> annotationBatch.setExportTaskId(taskId)).toList();
    return ExportTask.builder()
        .id(taskId)
        .jobId(jobId)
        .annotationBatches(toSave)
        // no need to set status as default getStatus will return pending unknown on empty
        .statusHistory(List.of())
        .build();
  }
}
