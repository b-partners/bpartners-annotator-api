package api.bpartners.annotator.endpoint.event;

import static api.bpartners.annotator.integration.JobIT.creatableCrupdateAnnotatedJob;
import static api.bpartners.annotator.repository.model.enums.TaskStatus.TO_CORRECT;
import static java.time.Instant.now;
import static java.util.Objects.requireNonNull;
import static java.util.UUID.randomUUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import api.bpartners.annotator.conf.FacadeIT;
import api.bpartners.annotator.endpoint.event.gen.AnnotatedJobCrupdated;
import api.bpartners.annotator.endpoint.rest.controller.mapper.AnnotationBatchMapper;
import api.bpartners.annotator.endpoint.rest.controller.mapper.JobMapper;
import api.bpartners.annotator.endpoint.rest.model.AnnotatedTask;
import api.bpartners.annotator.endpoint.rest.model.CrupdateAnnotatedJob;
import api.bpartners.annotator.integration.JobIT;
import api.bpartners.annotator.mail.Mailer;
import api.bpartners.annotator.repository.jpa.JobRepository;
import api.bpartners.annotator.repository.model.AnnotationBatch;
import api.bpartners.annotator.repository.model.Job;
import api.bpartners.annotator.repository.model.Task;
import api.bpartners.annotator.service.AnnotationBatchService;
import api.bpartners.annotator.service.TaskService;
import api.bpartners.annotator.service.event.CrupdateAnnotatedJobInitiatedService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

public class CrupdateAnnotatedJobInitiatedTriggeredIT extends FacadeIT {
  @Autowired CrupdateAnnotatedJobInitiatedService subject;
  @Autowired JobRepository jobRepository;
  @Autowired JobMapper jobMapper;
  @Autowired AnnotationBatchMapper restMapper;
  @MockBean TaskService taskServiceMock;
  @MockBean AnnotationBatchService annotationBatchServiceMock;
  @MockBean Mailer mailer;

  @BeforeEach
  void setup() {
    when(taskServiceMock.save(any())).thenAnswer(i -> i.<Task>getArgument(0));
    when(annotationBatchServiceMock.annotateTask(any()))
        .thenAnswer(i -> i.<AnnotationBatch>getArgument(0));
  }

  @Test
  void save_annotations_ok() {
    String jobId = randomUUID().toString();
    // updateBatchesCreationDatetime to real value because mockito.verify
    // does not support null values on annotation.creationDatetime and checking  batches is needed
    // in our verify tests
    var updatedCrupdateAnnotatedJob =
        updateBatchesCreationDatetime(
            creatableCrupdateAnnotatedJob(jobId, JobIT.creatableDummyLabel()));
    var createdJob = jobRepository.save(jobMapper.toDomain(updatedCrupdateAnnotatedJob));
    List<Task> extractedTasks = new ArrayList<>();
    List<AnnotationBatch> extractedBatches = new ArrayList<>();

    subject.accept(new AnnotatedJobCrupdated(updatedCrupdateAnnotatedJob, createdJob));

    updatedCrupdateAnnotatedJob
        .getAnnotatedTasks()
        .forEach(
            at -> {
              Task extractedTask = extract(at, createdJob);
              extractedTasks.add(extractedTask);
              extractedBatches.add(extract(at, extractedTask));
            });

    extractedTasks.forEach(
        t -> {
          verify(taskServiceMock, times(1)).save(t);
        });
    extractedBatches.forEach(
        b -> {
          verify(annotationBatchServiceMock, times(1)).annotateTask(b);
        });
    verify(mailer, times(1)).accept(any());
  }

  private Task extract(AnnotatedTask annotatedTask, Job job) {
    return Task.builder()
        .id(annotatedTask.getId())
        .filename(annotatedTask.getFilename())
        .job(job)
        .userId(annotatedTask.getAnnotatorId())
        .status(TO_CORRECT)
        .build();
  }

  private AnnotationBatch extract(AnnotatedTask annotatedTask, Task task) {
    return restMapper.toDomain(
        annotatedTask.getAnnotatorId(),
        task.getId(),
        requireNonNull(annotatedTask.getAnnotationBatch()));
  }

  private CrupdateAnnotatedJob updateBatchesCreationDatetime(CrupdateAnnotatedJob original) {
    var updatedAnnotatedTasks =
        original.getAnnotatedTasks().stream()
            .peek(
                at -> {
                  var annotationBatch = at.getAnnotationBatch();
                  if (annotationBatch.getCreationDatetime() == null) {
                    annotationBatch.setCreationDatetime(now());
                  }
                  at.setAnnotationBatch(annotationBatch);
                })
            .toList();
    original.setAnnotatedTasks(updatedAnnotatedTasks);
    return original;
  }
}
