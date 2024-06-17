package api.bpartners.annotator.repository.jpa;

import api.bpartners.annotator.repository.model.AnnotationBatchPageExport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnotationBatchPageExportRepository
    extends JpaRepository<AnnotationBatchPageExport, String> {}
