package api.bpartners.annotator.repository.jpa;

import api.bpartners.annotator.repository.model.JobExportStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobExportStatusRepository extends JpaRepository<JobExportStatus, String> {}
