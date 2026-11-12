package com.roles.usermanagement.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Catálogo de Permiso")
public record PermissionCatalogDto(
        @Schema(description = "Nombre único del permiso", example = "TICKET_READ_ALL")
        String name
) {}
