package api.bpartners.annotator.repository.dao;

import api.bpartners.annotator.repository.model.Task;
import api.bpartners.annotator.repository.model.enums.TaskStatus;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class TaskDao {
  private final EntityManager entityManager;

  public List<Task> findAllByJobIdAndStatusAndUserId(
      String jobId, TaskStatus status, String userId, Pageable pageable) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Task> query = cb.createQuery(Task.class);
    Root<Task> taskRoot = query.from(Task.class);

    List<Predicate> predicates = new ArrayList<>();

    if (jobId != null) {
      predicates.add(cb.equal(taskRoot.get("job").get("id"), jobId));
    }

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
}
