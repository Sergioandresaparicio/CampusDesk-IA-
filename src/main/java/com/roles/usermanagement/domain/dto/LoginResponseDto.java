package com.roles.usermanagement.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {

    @Schema(description = "Token JWT emitido")
    private String token;

    @Schema(description = "Nombre de usuario autenticado")
    private String username;

    @Schema(description = "Indica si debe cambiar la contraseña obligatoriamente")
    private Boolean mustChangePassword;
}
