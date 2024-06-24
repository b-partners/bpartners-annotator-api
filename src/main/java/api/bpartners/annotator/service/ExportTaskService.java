package api.bpartners.annotator.service;

import api.bpartners.annotator.repository.jpa.ExportTaskRepository;
import api.bpartners.annotator.repository.model.ExportTask;
import java.util.List;

import jakarta.transaction.Transactional;
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
}
