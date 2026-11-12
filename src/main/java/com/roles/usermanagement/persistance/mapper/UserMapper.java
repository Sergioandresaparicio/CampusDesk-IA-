package com.roles.usermanagement.persistance.mapper;

import com.roles.usermanagement.domain.dto.UserDto;
import com.roles.usermanagement.persistance.entity.PermissionEntity;
import com.roles.usermanagement.persistance.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "role", expression = "java(roleName(userEntity))")
    @Mapping(target = "additionalPermissions", expression = "java(permissionNames(userEntity))")
    UserDto toUserDto(UserEntity userEntity);

    List<UserDto> toUsersDto(List<UserEntity> userEntities);

    @Mapping(target = "additionalPermissions", ignore = true)
    UserEntity toUserEntity(UserDto userDto);

    default String roleName(UserEntity user) {
        return (user.getRoles() == null || user.getRoles().isEmpty())
                ? null
                : user.getRoles().get(0).getRole();
    }

    default List<String> permissionNames(UserEntity user) {
        if (user.getAdditionalPermissions() == null) {
            return List.of();
        }
        return user.getAdditionalPermissions().stream()
                .map(PermissionEntity::getName)
                .sorted()
                .toList();
    }
}
