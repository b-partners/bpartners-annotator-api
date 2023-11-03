package api.bpartners.annotator.endpoint.rest.controller;

import api.bpartners.annotator.endpoint.rest.controller.mapper.JobMapper;
import api.bpartners.annotator.endpoint.rest.controller.mapper.JobStatusMapper;
import api.bpartners.annotator.endpoint.rest.model.CrupdateJob;
import api.bpartners.annotator.endpoint.rest.model.ExportFormat;
import api.bpartners.annotator.endpoint.rest.model.Job;
import api.bpartners.annotator.endpoint.rest.model.JobStatus;
import api.bpartners.annotator.model.BoundedPageSize;
import api.bpartners.annotator.model.PageFromOne;
import api.bpartners.annotator.service.ExportService;
import api.bpartners.annotator.service.JobService;
import api.bpartners.annotator.service.utils.ByteWriter;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@AllArgsConstructor
public class JobController {
  private final JobService service;
  private final JobMapper mapper;
  private final JobStatusMapper statusMapper;
  private final ExportService exportService;
  private final ByteWriter byteWriter;
  private static final String EXPORTED_FILENAME_FORMAT = "%s_%s.json";

  @GetMapping("/jobs")
  public List<Job> getJobs(
      @RequestParam(defaultValue = "1", required = false) PageFromOne page,
      @RequestParam(defaultValue = "30", required = false) BoundedPageSize pageSize,
      @RequestParam(required = false) JobStatus status) {
    return service.getAllByStatus(page, pageSize, statusMapper.toDomain(status)).stream()
        .map(mapper::toRest)
        .toList();
  }

  @GetMapping("/jobs/{jobId}")
  public Job getJob(@PathVariable String jobId) {
    return mapper.toRest(service.getById(jobId));
  }

  @PutMapping("/jobs/{jobId}")
  public Job saveJob(@PathVariable String jobId, @RequestBody CrupdateJob job) {
    return mapper.toRest(service.save(mapper.toDomain(job)));
  }

  @GetMapping(
      value = "/jobs/{jobId}/export",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<byte[]> export(
      @PathVariable String jobId, @RequestParam("format") ExportFormat exportFormat) {
    var result = exportService.exportJob(jobId, exportFormat);
    var bytes = byteWriter.apply(result);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setContentDispositionFormData("attachment", String.format(EXPORTED_FILENAME_FORMAT, jobId, exportFormat));
    return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
  }
}
