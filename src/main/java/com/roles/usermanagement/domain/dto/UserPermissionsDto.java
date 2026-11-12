package com.roles.usermanagement.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Detalle completo de permisos efectivos del usuario")
public record UserPermissionsDto(
        @Schema(description = "Nombre de usuario", example = "sergio_admin")
        String username,
        @Schema(description = "Rol asignado", example = "ADMIN")
        String role,
        @Schema(description = "Permisos heredados del rol")
        List<String> rolePermissions,
        @Schema(description = "Permisos adicionales concedidos directamente")
        List<String> additionalPermissions,
        @Schema(description = "Permisos efectivos consolidados")
        List<String> effectivePermissions
) {}
