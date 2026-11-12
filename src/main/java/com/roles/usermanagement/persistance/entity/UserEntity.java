package com.roles.usermanagement.persistance.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents the User entity in the database with RBAC roles and granular permissions.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "\"user\"")
public class UserEntity {

    @Id
    @Column(nullable = false, length = 50, unique = true)
    private String username;

    @Column(name = "must_change_password", nullable = false)
    private Boolean mustChangePassword = true;

    @Column(nullable = false, length = 200, unique = true)
    private String email;

    @Column(nullable = false)
    private Boolean locked = false;

    @Column(nullable = false)
    private Boolean disabled = false;

    @Column(nullable = false, length = 200)
    private String password;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserRoleEntity> roles = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_permission",
            joinColumns = @JoinColumn(name = "username"),
            inverseJoinColumns = @JoinColumn(name = "permission_name"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"username", "permission_name"})
    )
    private Set<PermissionEntity> additionalPermissions = new LinkedHashSet<>();
}
