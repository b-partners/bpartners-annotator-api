package api.bpartners.annotator.repository.model;

import api.bpartners.annotator.endpoint.rest.security.model.Role;
import api.bpartners.annotator.repository.model.types.PostgresEnumType;
import io.hypersistence.utils.hibernate.type.array.EnumArrayType;
import io.hypersistence.utils.hibernate.type.array.internal.AbstractArrayType;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TypeDefs({
  @TypeDef(name = "pgsql_enum", typeClass = PostgresEnumType.class),
  @TypeDef(name = "user_roles", typeClass = EnumArrayType.class, defaultForType = Enum[].class),
  @TypeDef(
      name = "user_roles",
      typeClass = EnumArrayType.class,
      defaultForType = Role[].class,
      parameters = @Parameter(name = AbstractArrayType.SQL_ARRAY_TYPE, value = "user_role"))
})
@Table(name = "\"user\"")
public class User {
  @GeneratedValue @Id private String id;

  @ManyToOne
  @JoinColumn(name = "team_id", insertable = false, updatable = false)
  private Team team;

  @Type(type = "user_roles")
  @Column(name = "roles", columnDefinition = "user_role[]")
  private Role[] roles;

  private String email;
}
