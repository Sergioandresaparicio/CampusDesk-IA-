package com.roles.usermanagement.web.controller;

import com.roles.usermanagement.domain.dto.PermissionCatalogDto;
import com.roles.usermanagement.domain.dto.RoleCatalogDto;
import com.roles.usermanagement.domain.service.SecurityCatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalog")
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Catálogos de Seguridad", description = "Gestión administrativa de roles y permisos del sistema RBAC")
public class SecurityCatalogController {

    private final SecurityCatalogService catalog;

    public SecurityCatalogController(SecurityCatalogService catalog) {
        this.catalog = catalog;
    }

    @GetMapping("/roles")
    @Operation(summary = "Listar roles y sus permisos")
    public List<RoleCatalogDto> roles() {
        return catalog.roles();
    }

    @GetMapping("/permissions")
    @Operation(summary = "Listar todos los permisos del sistema")
    public List<String> permissions() {
        return catalog.permissions();
    }

    @PostMapping("/roles")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear nuevo rol")
    public RoleCatalogDto createRole(@Valid @RequestBody RoleCatalogDto request) {
        return catalog.createRole(request);
    }

    @PostMapping("/permissions")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear nuevo permiso")
    public PermissionCatalogDto createPermission(@Valid @RequestBody PermissionCatalogDto request) {
        return catalog.createPermission(request);
    }

    @PostMapping("/roles/{name}/permissions/{permission}")
    @Operation(summary = "Asociar permiso a rol")
    public RoleCatalogDto linkPermission(@PathVariable String name, @PathVariable String permission) {
        return catalog.link(name, permission, true);
    }

    @DeleteMapping("/roles/{name}/permissions/{permission}")
    @Operation(summary = "Desasociar permiso de rol")
    public RoleCatalogDto unlinkPermission(@PathVariable String name, @PathVariable String permission) {
        return catalog.link(name, permission, false);
    }
}
