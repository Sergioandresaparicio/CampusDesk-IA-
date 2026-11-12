package com.roles.usermanagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roles.usermanagement.domain.dto.LoginDto;
import com.roles.usermanagement.domain.dto.RegisterDto;
import com.roles.usermanagement.integrations.n8n.dto.N8nCallbackPayload;
import com.roles.usermanagement.integrations.n8n.dto.N8nTriggerRequest;
import com.roles.usermanagement.modules.ai.enums.AiQueryStatus;
import com.roles.usermanagement.modules.ticket.dto.TicketCreateRequest;
import com.roles.usermanagement.modules.ticket.dto.TicketStatusTransitionRequest;
import com.roles.usermanagement.modules.ticket.enums.TicketCategory;
import com.roles.usermanagement.modules.ticket.enums.TicketPriority;
import com.roles.usermanagement.modules.ticket.enums.TicketStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CampusDeskSmokeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Humo 1: Login Superadmin y consulta de perfil /api/auth/me")
    void testAuthLoginAndProfile() throws Exception {
        LoginDto loginDto = LoginDto.builder()
                .username("superadmin")
                .password("SuperAdmin123!")
                .build();

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.username", is("superadmin")))
                .andReturn();

        String responseBody = loginResult.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseBody).get("token").asText();

        // Query /api/auth/me
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("superadmin")))
                .andExpect(jsonPath("$.role", is("ADMIN")))
                .andExpect(jsonPath("$.effectivePermissions", hasItem("USER_READ")));
    }

    @Test
    @DisplayName("Humo 2: Registro de nuevo usuario y validación")
    void testUserRegistration() throws Exception {
        String testUser = "smoke_user_" + System.currentTimeMillis();
        RegisterDto registerDto = RegisterDto.builder()
                .username(testUser)
                .email(testUser + "@technova.com")
                .password("Password123!")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", is(testUser)))
                .andExpect(jsonPath("$.role", is("USER")));
    }

    @Test
    @DisplayName("Humo 3: Ciclo de vida de Incidencia y Estadísticas de Dashboard")
    void testTicketLifecycleAndDashboard() throws Exception {
        // Authenticate as superadmin
        LoginDto loginDto = LoginDto.builder().username("superadmin").password("SuperAdmin123!").build();
        String token = objectMapper.readTree(
                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginDto)))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString()
        ).get("token").asText();

        // 1. Create Ticket
        TicketCreateRequest createReq = TicketCreateRequest.builder()
                .title("Falla de acceso a servidor de archivos")
                .description("No se puede mapear la unidad de red departamental Z:")
                .category(TicketCategory.REDES)
                .priority(TicketPriority.ALTA)
                .room("Pabellón A")
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/tickets")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.status", is("OPEN")))
                .andReturn();

        long ticketId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("data").get("id").asLong();

        // 2. Transition to CANCELLED
        TicketStatusTransitionRequest transitionReq = TicketStatusTransitionRequest.builder()
                .status(TicketStatus.CANCELLED)
                .reason("Cancelado por duplicidad en reporte de usuario")
                .build();

        mockMvc.perform(put("/api/tickets/" + ticketId + "/status")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transitionReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status", is("CANCELLED")));

        // 3. Query Dashboard stats
        mockMvc.perform(get("/api/dashboard/stats")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.totalTickets", greaterThan(0)))
                .andExpect(jsonPath("$.data.resolutionRate", notNullValue()))
                .andExpect(jsonPath("$.data.averageResolutionMinutes", notNullValue()));
    }

    @Test
    @DisplayName("Humo 4: Integración M2M n8n mediante cabecera X-N8N-Api-Key")
    void testN8nM2mIntegration() throws Exception {
        // 1. Query /api/v1/integrations/n8n/data with API Key
        mockMvc.perform(get("/api/v1/integrations/n8n/data")
                        .header("X-N8N-Api-Key", "campusdesk-n8n-m2m-secret-key-2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.articlesForIndexing", notNullValue()))
                .andExpect(jsonPath("$.data.totalArticles", greaterThan(0)));

        // 2. Disallow M2M endpoint if invalid API Key (Access Denied)
        mockMvc.perform(get("/api/v1/integrations/n8n/data")
                        .header("X-N8N-Api-Key", "clave-invalida-123"))
                .andExpect(status().isForbidden());

        // 3. Trigger RAG Assistance (Fallback local)
        N8nTriggerRequest triggerReq = N8nTriggerRequest.builder()
                .ticketId(1L)
                .question("¿Cómo resolver error de desconexión WiFi?")
                .build();

        // Login as superadmin to trigger
        LoginDto loginDto = LoginDto.builder().username("superadmin").password("SuperAdmin123!").build();
        String token = objectMapper.readTree(
                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginDto)))
                        .andReturn().getResponse().getContentAsString()
        ).get("token").asText();

        MvcResult triggerResult = mockMvc.perform(post("/api/v1/integrations/n8n/trigger")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(triggerReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status", is("RESPONDIDA")))
                .andExpect(jsonPath("$.data.aiResponse", notNullValue()))
                .andReturn();

        long queryId = objectMapper.readTree(triggerResult.getResponse().getContentAsString())
                .get("data").get("queryId").asLong();

        // 4. Test n8n Callback with X-N8N-Api-Key
        N8nCallbackPayload callbackPayload = N8nCallbackPayload.builder()
                .queryId(queryId)
                .ticketId(1L)
                .status(AiQueryStatus.RESPONDIDA)
                .responseText("Solución validada por n8n: se sugiere reinicio manual de interface Gi0/1.")
                .suggestedSteps("1. Acceso a consola CLI. 2. shutdown / no shutdown.")
                .modelUsed("anthropic/claude-3.5-sonnet")
                .confidenceScore(0.97)
                .build();

        mockMvc.perform(post("/api/v1/integrations/n8n/callback")
                        .header("X-N8N-Api-Key", "campusdesk-n8n-m2m-secret-key-2026")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(callbackPayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.modelUsed", is("anthropic/claude-3.5-sonnet")));

        // 5. Rate the AI Response
        mockMvc.perform(post("/api/v1/integrations/n8n/queries/" + queryId + "/rate")
                        .header("Authorization", "Bearer " + token)
                        .param("score", "5")
                        .param("comment", "Respuesta exacta y verificable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.score", is(5)));
    }

    @Test
    @DisplayName("Humo 5: RF10 Regla 7 - Enviar descripción vacía debe rechazar el flujo inteligente")
    void testEmptyDescriptionRejection() throws Exception {
        LoginDto loginDto = LoginDto.builder().username("superadmin").password("SuperAdmin123!").build();
        String token = objectMapper.readTree(
                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginDto)))
                        .andReturn().getResponse().getContentAsString()
        ).get("token").asText();

        N8nTriggerRequest emptyReq = N8nTriggerRequest.builder()
                .ticketId(1L)
                .question("   ")
                .build();

        mockMvc.perform(post("/api/v1/integrations/n8n/trigger")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyReq)))
                .andExpect(status().isBadRequest());
    }
}
