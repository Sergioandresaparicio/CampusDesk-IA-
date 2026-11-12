package com.roles.usermanagement.persistance.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Represents the association between a User and an assigned Role.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "user_role",
        uniqueConstraints = @UniqueConstraint(name = "uk_user_role_username", columnNames = "username")
)
@IdClass(UserRoleId.class)
public class UserRoleEntity {

    @Id
    @Column(nullable = false, length = 50)
    private String username;

    @Id
    @Column(nullable = false, length = 50)
    private String role;

    @Column(nullable = false, name = "granted_date")
    private LocalDateTime grantedDate;

    @PrePersist
    protected void onCreate() {
        if (grantedDate == null) {
            grantedDate = LocalDateTime.now();
        }
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", referencedColumnName = "username", insertable = false, updatable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role", referencedColumnName = "name", insertable = false, updatable = false)
    private RoleEntity roleDefinition;
}
