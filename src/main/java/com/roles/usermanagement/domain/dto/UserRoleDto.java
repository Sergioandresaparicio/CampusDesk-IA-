package com.roles.usermanagement.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleDto {

    @Schema(description = "Nombre de usuario", example = "sergio_admin")
    private String username;

    @Schema(description = "Nombre del rol asignado", example = "ADMIN")
    private String role;

    @Schema(description = "Fecha de concesión del rol")
    private LocalDateTime grantedDate;
}
