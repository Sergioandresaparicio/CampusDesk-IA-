package com.roles.usermanagement.modules.knowledgebase.controller;

import com.roles.usermanagement.core.common.ApiResponse;
import com.roles.usermanagement.core.common.PageRequestDto;
import com.roles.usermanagement.core.common.PageResponse;
import com.roles.usermanagement.modules.knowledgebase.dto.KnowledgeArticleCreateDto;
import com.roles.usermanagement.modules.knowledgebase.dto.KnowledgeArticleDto;
import com.roles.usermanagement.modules.knowledgebase.service.KnowledgeBaseService;
import com.roles.usermanagement.web.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/knowledge-base")
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
@Tag(name = "Base de Conocimiento", description = "Artículos técnicos, procedimientos verificados y fuentes para RAG (RF02)")
public class KnowledgeBaseController {

    private final KnowledgeBaseService service;

    public KnowledgeBaseController(KnowledgeBaseService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN') or hasRole('TECHNICIAN')")
    @Operation(summary = "Crear nuevo artículo técnico")
    public ResponseEntity<ApiResponse<KnowledgeArticleDto>> createArticle(@Valid @RequestBody KnowledgeArticleCreateDto dto) {
        KnowledgeArticleDto created = service.createArticle(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Artículo de conocimiento creado con éxito", created));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar artículos activos y verificados (paginado)")
    public ResponseEntity<ApiResponse<PageResponse<KnowledgeArticleDto>>> getActiveArticles(@Valid PageRequestDto pageRequest) {
        PageResponse<KnowledgeArticleDto> page = service.getActiveArticles(pageRequest);
        return ResponseEntity.ok(ApiResponse.success(page));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener artículo técnico por ID")
    public ResponseEntity<ApiResponse<KnowledgeArticleDto>> getArticle(@PathVariable Long id) {
        KnowledgeArticleDto article = service.getArticle(id);
        return ResponseEntity.ok(ApiResponse.success(article));
    }

    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Búsqueda por palabras clave en artículos técnicos")
    public ResponseEntity<ApiResponse<List<KnowledgeArticleDto>>> search(@RequestParam(required = false) String query) {
        List<KnowledgeArticleDto> results = service.search(query);
        return ResponseEntity.ok(ApiResponse.success(results));
    }

    @PatchMapping("/{id}/toggle-active")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activar o desactivar artículo (artículos inactivos se excluyen del motor RAG)")
    public ResponseEntity<ApiResponse<KnowledgeArticleDto>> toggleActive(@PathVariable Long id, @RequestParam boolean active) {
        KnowledgeArticleDto updated = service.toggleActive(id, active);
        return ResponseEntity.ok(ApiResponse.success("Estado del artículo actualizado", updated));
    }
}
