package com.roles.usermanagement.persistance.mapper;

import com.roles.usermanagement.domain.dto.UserRoleDto;
import com.roles.usermanagement.persistance.entity.UserRoleEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserRoleMapper {

    UserRoleDto toUserRoleDto(UserRoleEntity userRoleEntity);

    List<UserRoleDto> toUserRolesDto(List<UserRoleEntity> userRoleEntities);

    @InheritInverseConfiguration
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "roleDefinition", ignore = true)
    UserRoleEntity toUserRoleEntity(UserRoleDto userRoleDto);
}
