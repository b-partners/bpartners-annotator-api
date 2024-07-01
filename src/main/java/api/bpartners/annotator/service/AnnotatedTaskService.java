package api.bpartners.annotator.service;

import static api.bpartners.annotator.endpoint.rest.model.JobType.REVIEWING;
import static api.bpartners.annotator.repository.model.enums.JobStatus.STARTED;
import static api.bpartners.annotator.repository.model.enums.TaskStatus.TO_CORRECT;
import static java.util.Objects.requireNonNull;

import api.bpartners.annotator.endpoint.rest.controller.mapper.AnnotationBatchMapper;
import api.bpartners.annotator.endpoint.rest.model.CreateAnnotatedTask;
import api.bpartners.annotator.endpoint.rest.validator.CreateAnnotatedTasksValidator;
import api.bpartners.annotator.model.exception.BadRequestException;
import api.bpartners.annotator.repository.model.AnnotationBatch;
import api.bpartners.annotator.repository.model.Job;
import api.bpartners.annotator.repository.model.Task;
import java.util.List;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnnotatedTaskService implements BiFunction<CreateAnnotatedTask, Job, Task> {
  private final AnnotationBatchService annotationBatchService;
  private final AnnotationBatchMapper annotationBatchRestMapper;
  private final TaskService taskService;
  private final JobService jobService;
  private final CreateAnnotatedTasksValidator createAnnotatedTasksValidator;

  public AnnotatedTaskService(
      AnnotationBatchService annotationBatchService,
      AnnotationBatchMapper annotationBatchRestMapper,
      TaskService taskService,
      JobService jobService,
      CreateAnnotatedTasksValidator createAnnotatedTasksValidator) {
    this.annotationBatchService = annotationBatchService;
    this.annotationBatchRestMapper = annotationBatchRestMapper;
    this.taskService = taskService;
    this.jobService = jobService;
    this.createAnnotatedTasksValidator = createAnnotatedTasksValidator;
  }

  @Override
  public Task apply(CreateAnnotatedTask annotatedTask, Job linkedJob) {
    return saveAndAnnotateTask(annotatedTask, linkedJob);
  }

  @Transactional
  public List<Task> saveAll(String jobId, List<CreateAnnotatedTask> annotatedTasks) {
    Job linkedJob = jobService.getById(jobId);
    createAnnotatedTasksValidator.accept(annotatedTasks, jobId);
    checkCurrentJobState(linkedJob);
    return annotatedTasks.stream().map(annotatedTask -> apply(annotatedTask, linkedJob)).toList();
  }

  public void checkCurrentJobState(Job job) {
    var builder = new StringBuilder();
    if (!REVIEWING.equals(job.getType())) {
      builder
          .append("cannot add tasks to Job.Id = ")
          .append(job.getId())
          .append(", only type ")
          .append(REVIEWING)
          .append(" supports this action");
    }
    if (job.getStatus().equals(STARTED)) {
      builder.append("cannot add tasks to ").append(job.getStatus()).append(" Job.");
    }

    if (!builder.isEmpty()) {
      throw new BadRequestException(builder.toString());
    }
  }

  private Task saveAndAnnotateTask(CreateAnnotatedTask annotatedTask, Job linkedJob) {
    Task linkedTask =
        taskService.save(
            Task.builder()
                .id(annotatedTask.getId())
                .filename(annotatedTask.getFilename())
                .job(linkedJob)
                .userId(annotatedTask.getAnnotatorId())
                .status(TO_CORRECT)
                .build());
    AnnotationBatch linkedBatch =
        annotationBatchRestMapper.toDomain(
            annotatedTask.getAnnotatorId(),
            linkedTask.getId(),
            requireNonNull(annotatedTask.getAnnotationBatch()));
    annotationBatchService.annotateTask(linkedBatch);
    return linkedTask;
  }
}
