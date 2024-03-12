package api.bpartners.annotator.service;

import static api.bpartners.annotator.repository.model.enums.JobStatus.COMPLETED;
import static api.bpartners.annotator.repository.model.enums.JobStatus.PENDING;
import static api.bpartners.annotator.repository.model.enums.JobStatus.STARTED;
import static api.bpartners.annotator.repository.model.enums.JobStatus.TO_CORRECT;
import static api.bpartners.annotator.repository.model.enums.JobStatus.TO_REVIEW;
import static org.springframework.data.domain.Pageable.unpaged;
import static org.springframework.data.domain.Sort.Order.asc;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;

import api.bpartners.annotator.endpoint.event.EventProducer;
import api.bpartners.annotator.endpoint.event.gen.AnnotatedJobCrupdated;
import api.bpartners.annotator.endpoint.event.gen.JobCreated;
import api.bpartners.annotator.endpoint.event.gen.JobExportInitiated;
import api.bpartners.annotator.endpoint.rest.model.CrupdateAnnotatedJob;
import api.bpartners.annotator.endpoint.rest.model.ExportFormat;
import api.bpartners.annotator.endpoint.rest.model.JobType;
import api.bpartners.annotator.model.BoundedPageSize;
import api.bpartners.annotator.model.PageFromOne;
import api.bpartners.annotator.model.VGG;
import api.bpartners.annotator.model.exception.BadRequestException;
import api.bpartners.annotator.model.exception.NotFoundException;
import api.bpartners.annotator.model.exception.NotImplementedException;
import api.bpartners.annotator.repository.dao.JobDao;
import api.bpartners.annotator.repository.jpa.JobRepository;
import api.bpartners.annotator.repository.model.Annotation;
import api.bpartners.annotator.repository.model.AnnotationBatch;
import api.bpartners.annotator.repository.model.Job;
import api.bpartners.annotator.repository.model.Task;
import api.bpartners.annotator.repository.model.enums.JobStatus;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class JobService {
  private static final Sort JOB_SORT = Sort.by(asc("name"));
  private final JobRepository repository;
  private final JobDao dao;
  private final EventProducer eventProducer;
  private static final List<JobStatus> ANNOTATOR_READABLE_JOB_STATUSES =
      List.of(STARTED, TO_CORRECT);

  public static JobCreated toEventType(Job job, String nextContinuationToken) {
    return JobCreated.builder().nextContinuationToken(nextContinuationToken).job(job).build();
  }

  public List<Job> getAnnotatorReadableJobs(
      String teamId, String name, JobType type, PageFromOne page, BoundedPageSize pageSize) {
    return getAllByTeamAndStatusesAndName(
        teamId, name, type, ANNOTATOR_READABLE_JOB_STATUSES, page, pageSize);
  }

  public List<Job> getAllByTeamAndStatusesAndName(
      String teamId,
      String name,
      JobType type,
      Collection<JobStatus> statuses,
      PageFromOne page,
      BoundedPageSize pageSize) {
    Pageable pageable;
    if (page != null && pageSize != null) {
      pageable = PageRequest.of(page.getValue() - 1, pageSize.getValue(), JOB_SORT);
    } else {
      pageable = unpaged(JOB_SORT);
    }
    return dao.findAllByCriteria(teamId, name, type, statuses, pageable);
  }

  public Job getByTeamAndIdAndStatuses(String teamId, String id, Collection<JobStatus> statuses) {
    return repository
        .findByTeamIdAndIdAndStatusIn(teamId, id, statuses)
        .orElseThrow(
            () ->
                new NotFoundException(
                    "Job identified by team.id = "
                        + teamId
                        + " and id = "
                        + id
                        + "with status = oneOf( "
                        + statuses
                        + " ) not found"));
  }

  public List<Job> getAllByStatusAndName(
      PageFromOne page, BoundedPageSize pageSize, JobType type, JobStatus status, String name) {
    Pageable pageable = PageRequest.of(page.getValue() - 1, pageSize.getValue(), JOB_SORT);
    return dao.findAllByCriteria(
        null, name, type, status == null ? null : List.of(status), pageable);
  }

  public Job getById(String id) {
    return repository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Job identified by id = " + id + " not found"));
  }

  @Transactional
  public Job save(Job job) {
    if (!repository.existsById(job.getId()) && job.getStatus().equals(PENDING)) {
      var savedJob = repository.save(job);
      eventProducer.accept(List.of(toEventType(savedJob, null)));
      return savedJob;
    }
    return updateJob(job);
  }

  @Transactional
  public Job crupdateAnnotatedJob(
      String jobId, CrupdateAnnotatedJob crupdateAnnotatedJob, Job job) {
    Job updated = repository.save(job);
    eventProducer.accept(List.of(new AnnotatedJobCrupdated(crupdateAnnotatedJob, job)));
    return updated;
  }

  public Job updateJobStatus(String jobId, JobStatus status) {
    Job persisted = getById(jobId);
    persisted.setStatus(status);
    return updateJob(persisted);
  }

  public Job setToReview(String jobId) {
    return updateJobStatus(jobId, TO_REVIEW);
  }

  public Job rejectForCorrection(String jobId) {
    return updateJobStatus(jobId, TO_CORRECT);
  }

  private Job updateJob(Job job) {
    Job persisted = getById(job.getId());
    checkJobStatusTransition(persisted, job);
    job.setLabels(persisted.getLabels());
    job.setTasks(persisted.getTasks());
    job.setBucketName(persisted.getBucketName());
    job.setFolderPath(persisted.getFolderPath());
    return repository.save(job);
  }

  public JobStatus checkJobStatusTransition(Job currentJob, Job newJob) {
    JobStatus current = currentJob.getStatus();
    JobStatus next = newJob.getStatus();
    BadRequestException exception =
        new BadRequestException(String.format("illegal transition: %s -> %s", current, next));
    return switch (current) {
      case PENDING -> switch (next) {
        case PENDING, READY, FAILED -> next;
        case STARTED, TO_REVIEW, TO_CORRECT, COMPLETED -> throw exception;
      };
      case READY -> switch (next) {
        case READY, STARTED, FAILED -> next;
        case PENDING, TO_REVIEW, TO_CORRECT, COMPLETED -> throw exception;
      };
      case STARTED -> switch (next) {
        case STARTED, TO_REVIEW, TO_CORRECT, FAILED -> next;
        case PENDING, READY, COMPLETED -> throw exception;
      };
      case TO_CORRECT -> switch (next) {
        case TO_CORRECT, STARTED, TO_REVIEW, COMPLETED, FAILED -> next;
        case PENDING, READY -> throw exception;
      };
      case TO_REVIEW -> switch (next) {
        case TO_REVIEW, TO_CORRECT, COMPLETED, FAILED -> next;
        case PENDING, READY, STARTED -> throw exception;
      };
      case FAILED -> throw new BadRequestException(
          "Failed Job cannot be changed, create new Job instead");
      case COMPLETED -> switch (next) {
        case COMPLETED, TO_REVIEW, TO_CORRECT -> next;
        case PENDING, READY, STARTED, FAILED -> throw exception;
      };
    };
  }

  @Transactional
  public Job refresh(String jobId) {
    Job persisted = getById(jobId);

    if (persisted.isCompleted()) {
      return persisted;
    }
    List<Task> tasks = persisted.getTasks();
    if (tasks.stream().allMatch(Task::isToReview)) {
      persisted.setStatus(TO_REVIEW);
    }
    if (tasks.stream().allMatch(Task::isCompleted)) {
      persisted.setStatus(COMPLETED);
    }

    return save(persisted);
  }

  @Service
  @AllArgsConstructor
  public static class ExportService {
    private final AnnotationBatchService annotationBatchService;
    private final EventProducer eventProducer;
    private final JobService jobService;

    public void initiateJobExport(String jobId, ExportFormat exportFormat) {
      var linkedJob = jobService.getById(jobId);
      eventProducer.accept(List.of(new JobExportInitiated(linkedJob, exportFormat)));
    }

    @Transactional(propagation = REQUIRED, readOnly = true, rollbackFor = Exception.class)
    public Object exportJob(String jobId, ExportFormat format) {
      if (ExportFormat.VGG.equals(format)) {
        return exportToVgg(jobId);
      }
      if (ExportFormat.COCO.equals(format)) {
        throw new NotImplementedException("COCO format has not been implemented yet");
      }
      throw new BadRequestException("unknown export format " + format);
    }

    private VGG exportToVgg(String jobId) {
      List<AnnotationBatch> latestPerTaskByJobId =
          annotationBatchService.findLatestPerTaskByJobId(jobId);
      VGG vgg = new VGG();
      latestPerTaskByJobId.forEach(batch -> vgg.put(batch.getTask().getFilename(), toVgg(batch)));
      return vgg;
    }

    private static VGG.Annotation toVgg(AnnotationBatch batch) {
      var vggAnnotation = new VGG.Annotation();
      // <-- UNUSED_DATA put at default value
      vggAnnotation.setFileAttributes(Map.of());
      vggAnnotation.setSize(null);
      vggAnnotation.setBase64ImageData(null);
      // UNUSED_DATA -->

      vggAnnotation.setFilename(batch.getTask().getFilename());
      var vggRegions = getVggRegions(batch);
      vggAnnotation.setRegions(vggRegions);
      return vggAnnotation;
    }

    private static Map<String, VGG.Annotation.Region> getVggRegions(AnnotationBatch batch) {
      var regions = new HashMap<String, VGG.Annotation.Region>();
      List<Annotation> annotations = batch.getAnnotations();
      for (int i = 0; i < annotations.size(); i++) {
        var annotation = annotations.get(i);
        var region = new VGG.Annotation.Region();

        region.setShapeAttribute(getShapeAttributes(annotation));
        region.setRegionAttribute(getRegionAttributes(annotation));

        regions.put(String.valueOf(i), region);
      }
      return regions;
    }

    private static VGG.Annotation.Region.RegionAttribute getRegionAttributes(Annotation annotation) {
      var regionAttributes = new VGG.Annotation.Region.RegionAttribute();
      regionAttributes.setLabel(annotation.getLabel().getName());
      return regionAttributes;
    }

    private static VGG.Annotation.Region.ShapeAttribute getShapeAttributes(Annotation annotation) {
      var shapeAttributes = new VGG.Annotation.Region.ShapeAttribute();
      var polygonPoints = annotation.getPolygon().getPoints();
      List<Double> allPointsX = new ArrayList<>();
      List<Double> allPointsY = new ArrayList<>();
      polygonPoints.forEach(
          point -> {
            allPointsX.add(point.getX());
            allPointsY.add(point.getY());
          });
      shapeAttributes.setName("polygon");
      shapeAttributes.setAllPointsX(allPointsX);
      shapeAttributes.setAllPointsY(allPointsY);
      return shapeAttributes;
    }
  }

  @Service
  @AllArgsConstructor
  public static class ImportService {
    public Job createJobFrom(){
      VGG vgg = new VGG();
      return new Job();
    }
  }
}
