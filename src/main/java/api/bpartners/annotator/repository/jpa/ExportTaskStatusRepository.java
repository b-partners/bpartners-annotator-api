package api.bpartners.annotator.repository.jpa;

import api.bpartners.annotator.repository.model.ExportTaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExportTaskStatusRepository extends JpaRepository<ExportTaskStatus, String> {}
