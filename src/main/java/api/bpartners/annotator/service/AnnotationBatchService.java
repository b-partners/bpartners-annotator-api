package api.bpartners.annotator.service;

import static api.bpartners.annotator.repository.model.enums.JobStatus.STARTED;
import static api.bpartners.annotator.repository.model.enums.JobStatus.TO_CORRECT;
import static org.springframework.data.domain.Sort.Direction.DESC;

import api.bpartners.annotator.model.BoundedPageSize;
import api.bpartners.annotator.model.PageFromOne;
import api.bpartners.annotator.model.exception.BadRequestException;
import api.bpartners.annotator.model.exception.NotFoundException;
import api.bpartners.annotator.repository.jpa.AnnotationBatchRepository;
import api.bpartners.annotator.repository.model.AnnotationBatch;
import api.bpartners.annotator.repository.model.Job;
import api.bpartners.annotator.repository.model.Task;
import api.bpartners.annotator.repository.model.enums.JobStatus;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AnnotationBatchService {
  private final AnnotationBatchRepository repository;
  private final TaskService taskService;

  @Transactional
  public AnnotationBatch annotateAndSetTaskToReview(AnnotationBatch annotationBatch) {
    Task linkedTask = annotationBatch.getTask();
    Job linkedTaskJob = linkedTask.getJob();
    JobStatus currentJobStatus = linkedTaskJob.getStatus();
    if (!STARTED.equals(currentJobStatus) && !TO_CORRECT.equals(currentJobStatus)) {
      throw new BadRequestException(
          "cannot annotate not (started or to_correct) job.Id = " + linkedTaskJob.getId());
    }
    if (isTaskNotAnnotable(linkedTask.getId())) {
      throw new BadRequestException(
          "Task.Id = " + linkedTask.getId() + " is already " + linkedTask.getStatus().name());
    }
    taskService.setToReview(linkedTask.getId());
    return repository.save(annotationBatch);
  }

  private boolean isTaskNotAnnotable(String taskId) {
    Task task = taskService.getById(taskId);
    return task.isCompleted() || task.isToReview();
  }

  public List<AnnotationBatch> findAllByTask(
      String taskId, PageFromOne page, BoundedPageSize pageSize) {
    Pageable pageable =
        PageRequest.of(
            page.getValue() - 1, pageSize.getValue(), Sort.by(DESC, "creationTimestamp"));
    return repository.findAllByTaskId(taskId, pageable);
  }

  public List<AnnotationBatch> findAllByAnnotatorIdAndTask(
      String annotatorId, String taskId, PageFromOne page, BoundedPageSize pageSize) {
    Pageable pageable =
        PageRequest.of(
            page.getValue() - 1, pageSize.getValue(), Sort.by(DESC, "creationTimestamp"));
    return repository.findAllByAnnotatorIdAndTaskId(annotatorId, taskId, pageable);
  }

  public AnnotationBatch findByTaskIdAndId(String taskId, String id) {
    return repository
        .findByTaskIdAndId(taskId, id)
        .orElseThrow(
            () ->
                new NotFoundException(
                    "AnnotationBatch identified by id = "
                        + id
                        + " and taskId = "
                        + taskId
                        + " not found"));
  }

  public AnnotationBatch findByAnnotatorIdAndTaskIdAndId(
      String annotatorId, String taskId, String id) {
    return repository
        .findByAnnotatorIdAndTaskIdAndId(annotatorId, taskId, id)
        .orElseThrow(
            () ->
                new NotFoundException(
                    "AnnotationBatch identified by id = "
                        + id
                        + " and taskId = "
                        + taskId
                        + " not found"));
  }

  public List<AnnotationBatch> findLatestPerTaskByJobId(String jobId) {
    return repository.findLatestPerTaskByJobId(jobId);
  }
}
