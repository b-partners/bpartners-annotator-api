package api.bpartners.annotator.service;

import api.bpartners.annotator.model.BoundedPageSize;
import api.bpartners.annotator.model.PageFromOne;
import api.bpartners.annotator.repository.jpa.TeamRepository;
import api.bpartners.annotator.repository.model.Team;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TeamService {
  private final TeamRepository repository;

  public List<Team> getAll(PageFromOne page, BoundedPageSize pageSize) {
    int pageValue = page != null ? page.getValue() - 1 : 0;
    int pageSizeValue = pageSize != null ? pageSize.getValue() : 30;
    Pageable pageable = PageRequest.of(pageValue, pageSizeValue);
    Page<Team> responses = repository.findAll(pageable);
    return responses.getContent();
  }

  public List<Team> saveAll(List<Team> toSave) {
    return repository.saveAll(toSave);
  }
}
