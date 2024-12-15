package rememberit.permission;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import rememberit.permission.types.PermissionType;

@Entity
@Table(name = "permissions")
@Getter
@Setter
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    public PermissionType type;
}