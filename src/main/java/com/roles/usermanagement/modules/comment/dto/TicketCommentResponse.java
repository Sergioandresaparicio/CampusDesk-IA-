package com.roles.usermanagement.modules.comment.dto;

import com.roles.usermanagement.modules.comment.enums.CommentVisibility;
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
public class TicketCommentResponse {

    @Schema(description = "ID del comentario")
    private Long id;

    @Schema(description = "ID del ticket asociado")
    private Long ticketId;

    @Schema(description = "Autor del comentario")
    private String authorUsername;

    @Schema(description = "Contenido")
    private String content;

    @Schema(description = "Visibilidad")
    private CommentVisibility visibility;

    @Schema(description = "Fecha de publicación")
    private LocalDateTime createdAt;
}
