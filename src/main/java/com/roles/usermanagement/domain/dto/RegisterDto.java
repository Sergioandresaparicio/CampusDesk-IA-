package com.roles.usermanagement.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDto {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(max = 50, message = "El nombre de usuario debe tener como máximo 50 caracteres")
    @Schema(description = "Nombre de usuario único", example = "sergio_user")
    private String username;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El formato del correo electrónico es inválido")
    @Size(max = 200, message = "El correo electrónico debe tener como máximo 200 caracteres")
    @Schema(description = "Correo electrónico institucional", example = "sergio@technova.com")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[^a-zA-Z0-9\\s]).{6,}$",
            message = "La contraseña debe tener al menos 6 caracteres, una letra mayúscula y un carácter especial"
    )
    @Schema(description = "Contraseña segura", example = "Password123!")
    private String password;
}
