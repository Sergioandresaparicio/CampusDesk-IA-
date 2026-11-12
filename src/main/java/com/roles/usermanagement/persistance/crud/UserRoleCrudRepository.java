package com.roles.usermanagement.persistance.crud;

import com.roles.usermanagement.persistance.entity.UserRoleEntity;
import com.roles.usermanagement.persistance.entity.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleCrudRepository extends JpaRepository<UserRoleEntity, UserRoleId> {

    List<UserRoleEntity> findByUsername(String username);

    void deleteByUsername(String username);
}
