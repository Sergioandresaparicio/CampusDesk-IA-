package com.roles.usermanagement.web.controller;

import com.roles.usermanagement.core.common.ApiResponse;
import com.roles.usermanagement.domain.dto.*;
import com.roles.usermanagement.domain.service.UserService;
import com.roles.usermanagement.web.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
@Tag(name = "Usuarios", description = "Administración de usuarios, asignación de roles y permisos")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar todos los usuarios del sistema")
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.success(userService.getAllUsers()));
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear nuevo usuario administrativo")
    public ResponseEntity<ApiResponse<UserDto>> addUser(@Valid @RequestBody UserDto userDto) {
        UserDto created = userService.saveUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Usuario creado con éxito", created));
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar datos de usuario")
    public ResponseEntity<ApiResponse<UserDto>> updateUser(@Valid @RequestBody UserDto userDto) {
        UserDto updated = userService.updateUser(userDto);
        return ResponseEntity.ok(ApiResponse.success("Usuario actualizado con éxito", updated));
    }

    @DeleteMapping("/delete/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar usuario")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return ResponseEntity.ok(ApiResponse.success("Usuario eliminado", null));
    }

    @PostMapping("/assignRole")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Asignar rol a usuario")
    public ResponseEntity<ApiResponse<UserRoleDto>> assignRole(@Valid @RequestBody UserRoleDto userRoleDto) {
        UserRoleDto result = userService.assignRoleToUser(userRoleDto);
        return ResponseEntity.ok(ApiResponse.success("Rol asignado con éxito", result));
    }

    @PostMapping("/assignPermission")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Conceder permiso individual a usuario")
    public ResponseEntity<ApiResponse<UserDto>> assignPermission(@Valid @RequestBody UserPermissionDto dto) {
        UserDto result = userService.grantPermission(dto);
        return ResponseEntity.ok(ApiResponse.success("Permiso concedido", result));
    }

    @DeleteMapping("/{username}/permissions/{permission}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Revocar permiso individual")
    public ResponseEntity<ApiResponse<UserDto>> revokePermission(@PathVariable String username, @PathVariable String permission) {
        UserDto result = userService.revokePermission(username, permission);
        return ResponseEntity.ok(ApiResponse.success("Permiso revocado", result));
    }

    @GetMapping("/{username}/permissions")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Consultar permisos de un usuario")
    public ResponseEntity<ApiResponse<UserPermissionsDto>> getPermissions(@PathVariable String username) {
        return ResponseEntity.ok(ApiResponse.success(userService.permissionDetails(username)));
    }

    @GetMapping("/permissions/catalog")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Consultar catálogo de permisos")
    public ResponseEntity<ApiResponse<List<String>>> getPermissionCatalog() {
        return ResponseEntity.ok(ApiResponse.success(userService.permissionCatalog()));
    }
}
