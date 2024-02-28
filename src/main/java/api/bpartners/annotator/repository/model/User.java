package api.bpartners.annotator.repository.model;

import static io.hypersistence.utils.hibernate.type.array.internal.AbstractArrayType.SQL_ARRAY_TYPE;
import static jakarta.persistence.EnumType.STRING;
import static org.hibernate.type.SqlTypes.ARRAY;

import api.bpartners.annotator.endpoint.rest.security.model.Role;
import io.hypersistence.utils.hibernate.type.array.EnumArrayType;
import io.hypersistence.utils.hibernate.type.array.internal.AbstractArrayType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

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

  @Type(
    value = EnumArrayType.class,
    parameters = @Parameter(
      name = SQL_ARRAY_TYPE,
      value = "user_role"
    )
  )
  @Column(
    name = "roles",
    columnDefinition = "user_role[]"
  )
  private Role[] roles;

  private String email;
}
