package api.bpartners.annotator.repository.dao;

import static api.bpartners.annotator.repository.model.enums.TaskStatus.PENDING;
import static api.bpartners.annotator.repository.model.enums.TaskStatus.TO_CORRECT;
import static api.bpartners.annotator.repository.model.enums.TaskStatus.UNDER_COMPLETION;

import api.bpartners.annotator.repository.model.Task;
import api.bpartners.annotator.repository.model.enums.TaskStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class TaskDao {
  private static final List<TaskStatus> ANNOTATOR_OWNABLE_AVAILABLE_TASK_STATUSES =
      List.of(UNDER_COMPLETION, TO_CORRECT);
  private final EntityManager entityManager;

  public List<Task> findAllByJobIdAndStatusAndUserId(
      String jobId, TaskStatus status, String userId, Pageable pageable) {
    assert jobId != null : ("JOB ID must not be null");
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Task> query = cb.createQuery(Task.class);
    Root<Task> taskRoot = query.from(Task.class);
    List<Predicate> predicates = new ArrayList<>();

    predicates.add(cb.equal(taskRoot.get("job").get("id"), jobId));

    if (status != null) {
      predicates.add(cb.equal(taskRoot.get("status"), status));
    }

    if (userId != null) {
      predicates.add(cb.equal(taskRoot.get("userId"), userId));
    }

    query.where(predicates.toArray(new Predicate[0]));

    return entityManager
        .createQuery(query)
        .setFirstResult((pageable.getPageNumber()) * pageable.getPageSize())
        .setMaxResults(pageable.getPageSize())
        .getResultList();
  }

  public Optional<Task> findAvailableTaskFromJobOrJobAndUserIdOrJobAndExternalUserIds(
      @NotNull String jobId, @NotNull String userId, @NotNull List<String> externalUserIds) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Task> query = cb.createQuery(Task.class);
    Root<Task> taskRoot = query.from(Task.class);

    Predicate[] predicates = {
      cb.and(
          cb.equal(taskRoot.get("job").get("id"), jobId),
          cb.or(
              cb.and(taskRoot.get("userId").in(externalUserIds)),
              cb.and(
                  cb.equal(taskRoot.get("userId"), userId),
                  taskRoot.get("status").in(ANNOTATOR_OWNABLE_AVAILABLE_TASK_STATUSES)),
              cb.equal(taskRoot.get("status"), PENDING)))
    };

    query.where(predicates);
    try {
      return Optional.of(entityManager.createQuery(query).setMaxResults(1).getSingleResult());
    } catch (NoResultException e) {
      return Optional.empty();
    }
  }
}
