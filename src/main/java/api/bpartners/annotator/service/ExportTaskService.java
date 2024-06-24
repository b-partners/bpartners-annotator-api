package api.bpartners.annotator.service;

import api.bpartners.annotator.model.exception.NotFoundException;
import api.bpartners.annotator.repository.jpa.ExportTaskRepository;
import api.bpartners.annotator.repository.model.ExportTask;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ExportTaskService {
  private final ExportTaskRepository repository;

  @Transactional
  public List<ExportTask> saveAll(List<ExportTask> toSave) {
    return repository.saveAll(toSave);
  }

  public ExportTask getTaskById(String id) {
    return repository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Export.id=" + id + " is not found"));
  }
}
