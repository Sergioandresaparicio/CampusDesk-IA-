package com.roles.usermanagement.domain.repository;

import com.roles.usermanagement.domain.dto.UserDto;

import java.util.List;

/**
 * Interface defining operations to access and manage users in the domain.
 */
public interface IUserRepository {

    UserDto loadUserByUsername(String username);

    UserDto save(UserDto userDto);

    List<UserDto> getAllUsers();

    void deleteUser(String username);

    boolean existsByUsername(String username);
}
