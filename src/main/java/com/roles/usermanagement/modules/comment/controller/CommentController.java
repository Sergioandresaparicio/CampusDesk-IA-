package com.roles.usermanagement.modules.comment.controller;

import com.roles.usermanagement.core.common.ApiResponse;
import com.roles.usermanagement.modules.comment.dto.TicketCommentRequest;
import com.roles.usermanagement.modules.comment.dto.TicketCommentResponse;
import com.roles.usermanagement.modules.comment.service.TicketCommentService;
import com.roles.usermanagement.web.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
@Tag(name = "Comentarios", description = "Comentarios públicos entre usuarios y técnicos, y notas internas privadas")
public class CommentController {

    private final TicketCommentService commentService;

    public CommentController(TicketCommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/{id}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Agregar comentario a una incidencia")
    public ResponseEntity<ApiResponse<TicketCommentResponse>> addComment(
            @PathVariable Long id,
            @Valid @RequestBody TicketCommentRequest request,
            Principal principal,
            Authentication authentication
    ) {
        boolean isStaff = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")
                        || a.getAuthority().equals("ROLE_TECHNICIAN")
                        || a.getAuthority().equals("ROLE_M2M_N8N"));

        TicketCommentResponse response = commentService.addComment(id, request, principal.getName(), isStaff);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Comentario registrado", response));
    }

    @GetMapping("/{id}/comments")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar comentarios visibles de una incidencia")
    public ResponseEntity<ApiResponse<List<TicketCommentResponse>>> getComments(
            @PathVariable Long id,
            Authentication authentication
    ) {
        boolean isStaff = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")
                        || a.getAuthority().equals("ROLE_TECHNICIAN")
                        || a.getAuthority().equals("ROLE_M2M_N8N"));

        List<TicketCommentResponse> comments = commentService.getComments(id, isStaff);
        return ResponseEntity.ok(ApiResponse.success(comments));
    }
}
