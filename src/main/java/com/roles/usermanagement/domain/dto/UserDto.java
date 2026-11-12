package com.roles.usermanagement.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(max = 50, message = "El nombre de usuario debe tener como máximo 50 caracteres")
    @Schema(description = "Nombre de usuario único", example = "sergio_admin")
    private String username;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El correo electrónico debe ser válido")
    @Size(max = 200, message = "El correo electrónico debe tener como máximo 200 caracteres")
    @Schema(description = "Correo electrónico institucional", example = "sergio@technova.com")
    private String email;

    @Schema(description = "Indica si la cuenta está bloqueada")
    private Boolean locked;

    @Schema(description = "Indica si la cuenta está deshabilitada")
    private Boolean disabled;

    @Schema(description = "Indica si el usuario debe cambiar su contraseña obligatoriamente", example = "false")
    private Boolean mustChangePassword;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(description = "Contraseña de la cuenta (solo escritura)", example = "SuperAdmin123!")
    private String password;

    @Schema(description = "Rol único del usuario en el sistema", example = "ADMIN")
    private String role;

    @Schema(description = "Listado de roles del usuario (compatibilidad)")
    private List<UserRoleDto> roles;

    @Schema(description = "Permisos individuales adicionales al rol")
    private List<String> additionalPermissions;
}
