package com.roles.usermanagement.persistance.crud;

import com.roles.usermanagement.persistance.entity.UserEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCrudRepository extends JpaRepository<UserEntity, String> {

    @EntityGraph(attributePaths = {"roles", "roles.roleDefinition", "additionalPermissions"})
    @Override
    Optional<UserEntity> findById(String username);

    @EntityGraph(attributePaths = {"roles", "roles.roleDefinition", "additionalPermissions"})
    @Override
    List<UserEntity> findAll();

    @EntityGraph(attributePaths = {"roles", "roles.roleDefinition", "additionalPermissions"})
    Optional<UserEntity> findByEmail(String email);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from UserEntity u where u.username = :username")
    Optional<UserEntity> findForUpdate(@Param("username") String username);

    boolean existsByEmailAndUsernameNot(String email, String username);

    boolean existsByEmail(String email);
}
