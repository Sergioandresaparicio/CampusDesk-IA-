package com.roles.usermanagement.modules.comment.dto;

import com.roles.usermanagement.modules.comment.enums.CommentVisibility;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketCommentRequest {

    @NotBlank(message = "El contenido del comentario es obligatorio")
    @Schema(description = "Contenido del comentario o nota técnica", example = "Se realizó la medición de señal en el aula y llega a -65dBm.")
    private String content;

    @Schema(description = "Visibilidad del comentario (PUBLIC para usuario y técnico, INTERNAL solo para técnicos y admins)", example = "PUBLIC")
    private CommentVisibility visibility;
}
