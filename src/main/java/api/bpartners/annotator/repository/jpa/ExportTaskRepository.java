package api.bpartners.annotator.repository.jpa;

import api.bpartners.annotator.repository.model.ExportTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExportTaskRepository extends JpaRepository<ExportTask, String> {}
