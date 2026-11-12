package com.roles.usermanagement.persistance.crud;

import com.roles.usermanagement.persistance.entity.RoleEntity;
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
public interface RoleCrudRepository extends JpaRepository<RoleEntity, String> {

    @EntityGraph(attributePaths = {"permissions"})
    @Override
    Optional<RoleEntity> findById(String name);

    @EntityGraph(attributePaths = {"permissions"})
    @Override
    List<RoleEntity> findAll();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from RoleEntity r where r.name = :name")
    Optional<RoleEntity> findForUpdate(@Param("name") String name);
}
