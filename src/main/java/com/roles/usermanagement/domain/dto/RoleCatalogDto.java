package com.roles.usermanagement.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Catálogo de Rol con sus permisos asignados")
public record RoleCatalogDto(
        @Schema(description = "Nombre del rol", example = "ADMIN")
        String name,
        @Schema(description = "Lista de nombres de permisos")
        List<String> permissions
) {}
