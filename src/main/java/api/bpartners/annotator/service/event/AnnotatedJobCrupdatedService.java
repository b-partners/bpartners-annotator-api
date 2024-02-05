package api.bpartners.annotator.service.event;

import static api.bpartners.annotator.repository.model.enums.TaskStatus.TO_CORRECT;
import static java.util.Objects.requireNonNull;

import api.bpartners.annotator.endpoint.event.gen.AnnotatedJobCrupdated;
import api.bpartners.annotator.endpoint.rest.controller.mapper.AnnotationBatchMapper;
import api.bpartners.annotator.endpoint.rest.model.AnnotatedTask;
import api.bpartners.annotator.endpoint.rest.model.CrupdateAnnotatedJob;
import api.bpartners.annotator.repository.model.AnnotationBatch;
import api.bpartners.annotator.repository.model.Job;
import api.bpartners.annotator.repository.model.Task;
import api.bpartners.annotator.service.AnnotationBatchService;
import api.bpartners.annotator.service.TaskService;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AnnotatedJobCrupdatedService implements Consumer<AnnotatedJobCrupdated> {
  private final TaskService taskService;
  private final AnnotationBatchService annotationBatchService;
  private final AnnotationBatchMapper annotationBatchRestMapper;

  @Override
  @Transactional
  public void accept(AnnotatedJobCrupdated annotatedJobCrupdated) {
    CrupdateAnnotatedJob crupdateAnnotatedJob = annotatedJobCrupdated.getCrupdateAnnotatedJob();
    Job linkedJob = annotatedJobCrupdated.getJob();

    requireNonNull(crupdateAnnotatedJob.getAnnotatedTasks())
        .forEach(annotatedTask -> saveAndAnnotateTask(annotatedTask, linkedJob));
  }

  private void saveAndAnnotateTask(AnnotatedTask annotatedTask, Job savedJob) {
    Task linkedTask =
        taskService.save(
            Task.builder()
                .id(annotatedTask.getId())
                .filename(annotatedTask.getFilename())
                .job(savedJob)
                .userId(annotatedTask.getAnnotatorId())
                .status(TO_CORRECT)
                .build());
    AnnotationBatch linkedBatch =
        annotationBatchRestMapper.toDomain(
            annotatedTask.getAnnotatorId(),
            linkedTask.getId(),
            requireNonNull(annotatedTask.getAnnotationBatch()));
    annotationBatchService.annotateTask(linkedBatch);
  }
}
