package api.bpartners.annotator.repository.dao;

import api.bpartners.annotator.endpoint.rest.model.JobType;
import api.bpartners.annotator.repository.model.Job;
import api.bpartners.annotator.repository.model.enums.JobStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class JobDao {
  private final EntityManager entityManager;

  public List<Job> findAllByCriteria(
      String teamId,
      String name,
      JobType jobType,
      Collection<JobStatus> jobStatuses,
      Pageable pageable) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Job> query = cb.createQuery(Job.class);
    Root<Job> root = query.from(Job.class);
    List<Predicate> predicates = new ArrayList<>();

    if (jobType != null) {
      predicates.add(cb.equal(root.get("type"), jobType));
    }
    if (jobStatuses != null) {
      predicates.add(root.get("status").in(jobStatuses));
    }
    if (teamId != null) {
      predicates.add(cb.equal(root.get("teamId"), teamId));
    }
    if (name != null) {
      predicates.add(
          cb.or(
              cb.like(cb.lower(root.get("name")), "%" + name + "%"),
              cb.like(root.get("name"), "%" + name + "%")));
    }
    query.where(predicates.toArray(Predicate[]::new));

    return entityManager
        .createQuery(query)
        .setFirstResult((pageable.getPageNumber()) * pageable.getPageSize())
        .setMaxResults(pageable.getPageSize())
        .getResultList();
  }
}
