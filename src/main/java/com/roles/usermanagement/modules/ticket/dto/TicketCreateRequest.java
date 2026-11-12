package com.roles.usermanagement.modules.ticket.dto;

import com.roles.usermanagement.modules.ticket.enums.TicketCategory;
import com.roles.usermanagement.modules.ticket.enums.TicketPriority;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketCreateRequest {

    @NotBlank(message = "El título de la incidencia es obligatorio")
    @Size(max = 150, message = "El título no puede superar los 150 caracteres")
    @Schema(description = "Título descriptivo del incidente", example = "Falla de conexión WiFi en el aula 302")
    private String title;

    @NotBlank(message = "La descripción del problema es obligatoria")
    @Schema(description = "Descripción detallada del incidente", example = "Los equipos no obtienen dirección IP del router principal.")
    private String description;

    @NotNull(message = "La categoría es obligatoria")
    @Schema(description = "Categoría del incidente", example = "REDES")
    private TicketCategory category;

    @NotNull(message = "La prioridad es obligatoria")
    @Schema(description = "Prioridad asignada", example = "ALTA")
    private TicketPriority priority;

    @Schema(description = "Identificador del dispositivo o equipo", example = "Laptop Dell Latitude 5420")
    private String device;

    @Schema(description = "Ubicación o sala del incidente", example = "Edificio B, Aula 302")
    private String room;
}
