package com.roles.usermanagement.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {

    @Schema(description = "Nombre de usuario o correo electrónico", example = "superadmin")
    private String username;

    @Schema(description = "Correo electrónico alternativo", example = "superadmin@localhost")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Schema(description = "Contraseña de la cuenta", example = "SuperAdmin123!")
    private String password;

    public void setIdentifier(String identifier) {
        if (this.username == null) {
            this.username = identifier;
        }
    }

    public String getIdentifier() {
        if (email != null && !email.isBlank()) {
            return email.trim().toLowerCase();
        }
        if (username != null && !username.isBlank()) {
            return username.trim();
        }
        return null;
    }
}
