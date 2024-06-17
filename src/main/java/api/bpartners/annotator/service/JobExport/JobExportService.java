package api.bpartners.annotator.service.JobExport;

import static java.util.UUID.randomUUID;

import api.bpartners.annotator.endpoint.event.model.JobExportInitiated;
import api.bpartners.annotator.model.exception.NotFoundException;
import api.bpartners.annotator.repository.jpa.JobExportRepository;
import api.bpartners.annotator.repository.model.Job;
import api.bpartners.annotator.repository.model.JobExport;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class JobExportService {
  private final JobExportRepository repository;

  public final JobExport create(Job job, JobExportInitiated jobExportInitiated) {
    String id = jobExportInitiated.getId();
    var toSave =
        JobExport.builder()
            .id(id)
            // no need to set status as default Statusable::getStatus will return pending unknown on
            // empty
            .statuses(List.of())
            .folderPath(createAnnotationBatchExportFolderPathFrom(job))
            .emailCC(jobExportInitiated.getEmailCC().getAddress())
            .emailOwner(job.getOwnerEmail())
            .build();
    return repository.save(toSave);
  }

  public final JobExport findById(String id) {
    return repository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("JobExport.Id = " + id + " not found."));
  }

  private static String createAnnotationBatchExportFolderPathFrom(Job job) {
    return job.getName().endsWith("/")
        ? (job.getName())
        : (job.getName() + "/") + randomUUID() + "/";
  }
}
