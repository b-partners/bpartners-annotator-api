package api.bpartners.annotator.endpoint.rest.controller.adminonly;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

import api.bpartners.annotator.endpoint.rest.controller.mapper.JobMapper;
import api.bpartners.annotator.endpoint.rest.controller.mapper.JobStatusMapper;
import api.bpartners.annotator.endpoint.rest.model.CrupdateJob;
import api.bpartners.annotator.endpoint.rest.model.ExportFormat;
import api.bpartners.annotator.endpoint.rest.model.Job;
import api.bpartners.annotator.endpoint.rest.model.JobStatus;
import api.bpartners.annotator.endpoint.rest.model.JobType;
import api.bpartners.annotator.endpoint.rest.validator.CrupdateJobIdValidator;
import api.bpartners.annotator.model.BoundedPageSize;
import api.bpartners.annotator.model.PageFromOne;
import api.bpartners.annotator.service.JobExport.ExportService;
import api.bpartners.annotator.service.JobService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class JobController {
  private final JobService service;
  private final JobMapper mapper;
  private final JobStatusMapper statusMapper;
  private final ExportService exportService;
  private final CrupdateJobIdValidator crupdateJobIdValidator;

  @GetMapping("/jobs")
  public List<Job> getJobs(
      @RequestParam(defaultValue = "1", required = false) PageFromOne page,
      @RequestParam(defaultValue = "30", required = false) BoundedPageSize pageSize,
      @RequestParam(required = false) JobStatus status,
      @RequestParam(required = false) JobType type,
      @RequestParam(required = false) String name) {
    return service
        .getAllByStatusAndName(page, pageSize, type, statusMapper.toDomain(status), name)
        .stream()
        .map(mapper::toRest)
        .toList();
  }

  @GetMapping("/jobs/{jobId}")
  public Job getJob(@PathVariable String jobId) {
    return mapper.toRest(service.getById(jobId));
  }

  @GetMapping(
      value = "/jobs/{jobId}/annotationStatistics",
      produces = {TEXT_PLAIN_VALUE})
  public ResponseEntity<String> getJobLatestAnnotationStatisticsByMail(
      @PathVariable String jobId, @RequestParam(required = false) String emailCC) {
    service.fireAnnotationStatisticsComputationEvent(jobId, emailCC);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(TEXT_PLAIN);
    return new ResponseEntity<>("ok", headers, OK);
  }

  @PutMapping("/jobs/{jobId}")
  public Job saveJob(@PathVariable String jobId, @RequestBody CrupdateJob job) {
    crupdateJobIdValidator.accept(job, jobId);
    return mapper.toRest(service.save(mapper.toDomain(job)));
  }

  @GetMapping(
      value = "/jobs/{jobId}/export",
      produces = {TEXT_PLAIN_VALUE})
  public ResponseEntity<String> export(
      @PathVariable String jobId,
      @RequestParam("format") ExportFormat exportFormat,
      @RequestParam(required = false) String emailCC) {
    exportService.initiateJobExport(jobId, exportFormat, emailCC);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(TEXT_PLAIN);
    return new ResponseEntity<>("ok", headers, OK);
  }
}
