package api.bpartners.annotator.repository.model;

import static org.hibernate.type.SqlTypes.ARRAY;

import api.bpartners.annotator.endpoint.rest.security.model.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "\"user\"")
public class User {
  @Id private String id;

  @ManyToOne
  @JoinColumn(name = "team_id")
  private Team team;

  @JdbcTypeCode(ARRAY)
  private Role[] roles;

  private String email;
}
