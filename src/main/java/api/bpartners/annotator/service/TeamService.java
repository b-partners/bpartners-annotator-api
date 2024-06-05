package api.bpartners.annotator.service;

import static java.util.stream.Collectors.toList;

import api.bpartners.annotator.endpoint.event.EventProducer;
import api.bpartners.annotator.endpoint.event.model.TeamUpserted;
import api.bpartners.annotator.model.BoundedPageSize;
import api.bpartners.annotator.model.PageFromOne;
import api.bpartners.annotator.model.exception.NotFoundException;
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
  private final EventProducer eventProducer;

  public List<Team> getAll(PageFromOne page, BoundedPageSize pageSize) {
    Pageable pageable = PageRequest.of(page.getValue() - 1, pageSize.getValue());
    Page<Team> responses = repository.findAll(pageable);
    return responses.getContent();
  }

  public Team getById(String id) {
    return repository
        .findById(id)
        .orElseThrow(
            () -> new NotFoundException("Team identified by Team.Id = " + id + " not found. "));
  }

  public List<Team> fireEvents(List<Team> toSave) {
    eventProducer.accept(toSave.stream().map(this::toTypedTeam).collect(toList()));
    return toSave;
  }

  private TeamUpserted toTypedTeam(Team team) {
    return TeamUpserted.builder().team(team).build();
  }

  public Team save(Team team) {
    return repository.save(team);
  }
}
