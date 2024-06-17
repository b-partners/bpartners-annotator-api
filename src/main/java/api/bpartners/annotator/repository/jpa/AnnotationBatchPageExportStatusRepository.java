package api.bpartners.annotator.repository.jpa;

import api.bpartners.annotator.repository.model.AnnotationBatchPageExportStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnotationBatchPageExportStatusRepository
    extends JpaRepository<AnnotationBatchPageExportStatus, String> {}
