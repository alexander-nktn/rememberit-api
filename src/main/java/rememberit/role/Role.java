package rememberit.role;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import rememberit.permission.Permission;
import rememberit.role.types.RoleType;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    public RoleType name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    public Set<Permission> permissions;
}