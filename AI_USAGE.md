# Registro de Uso de Inteligencia Artificial (AI_USAGE.md)

## 1. Declaración General
En el marco del desarrollo del backend para **CampusDesk** (Sistema Inteligente de Análisis de Incidentes Tecnológicos y Asistencia mediante IA), se utilizó asistencia de Inteligencia Artificial (Antigravity AI / Google DeepMind) actuando como Arquitecto de Software Senior y Desarrollador Full-Stack en Java y Spring Boot.

---

## 2. Áreas de Asistencia e Intervención de la IA

### 2.1 Arquitectura de Software y Modelado
- **Diseño del Dominio:** Extracción de requisitos del documento funcional para modelar el ciclo de vida de tickets (`OPEN`, `IN_PROGRESS`, `PENDING`, `RESOLVED`, `CLOSED`, `CANCELLED`), el modelo de base de conocimiento (RAG), y el sistema de tracking de técnicos por especialidad y carga de trabajo.
- **Herencia Arquitectónica:** Análisis minucioso del proyecto de referencia `spring-security-user-role-management`, replicando su patrón de paquetes `com.roles.usermanagement.*`, su arquitectura desacoplada por capas (`persistance`, `domain`, `web`, `core`) y su modelo de persistencia RBAC (`UserEntity`, `RoleEntity`, `PermissionEntity`, `UserRoleEntity`).
- **Seguridad Dual (JWT + M2M):** Formulación de un esquema de seguridad Spring Security 6 que atiende simultáneamente usuarios humanos vía JWT (`Authorization: Bearer`) y agentes automáticos n8n vía token de servicio en cabecera HTTP (`X-N8N-Api-Key`), evitando conflictos de contexto de seguridad.

### 2.2 Generación de Código y Patrones de Diseño
- **Prevención de Problemas N+1:** Implementación de `@EntityGraph(attributePaths = ...)` en los repositorios JPA (`TicketRepository`, `UserCrudRepository`, `RoleCrudRepository`) para carga eager eficiente de relaciones críticas (`creator`, `assignedTechnician`, `statusHistory`, `userRoles`).
- **Consultas Dinámicas:** Uso de Spring Data JPA `JpaSpecificationExecutor` y predicados Criteria API para filtros compuestos en `/api/v1/tickets`.
- **Mapeo Tipado:** Configuración de MapStruct para transformaciones directas y seguras entre Entidades de Base de Datos y DTOs de capa web.
- **Manejo Centralizado de Excepciones:** `@RestControllerAdvice` con control tipado de errores de negocio (`ResourceNotFoundException`, `BusinessValidationException`), capturas de `MethodArgumentNotValidException` de Jakarta Validation y fallos de autenticación/autorización.

### 2.3 Resiliencia y Motor RAG Local de Respaldo
- **Integración con n8n:** Configuración de `RestClient` moderno de Spring 6 con timeouts definidos para disparar webhooks asíncronos y síncronos hacia n8n (`http://localhost:5678/webhook/campusdesk-ai`).
- **Fallback Engine:** Implementación de un motor heurístico de similitud de textos sobre la base de conocimiento local en `N8nIntegrationService`. Si n8n se encuentra apagado o no responde en el entorno de desarrollo, el backend responde automáticamente con fuentes verificadas y puntaje de confianza, garantizando que el frontend nunca sufra errores 500 por desconexión del flujo externo.

### 2.4 Control de Calidad y Pruebas de Humo
- **Pruebas de Integración con MockMvc:** Diseño de la suite `CampusDeskSmokeIntegrationTest` que valida de extremo a extremo:
  1. Autenticación exitosa y generación de JWT con credenciales sembradas.
  2. Registro de nuevo usuario con validación de roles por defecto.
  3. Creación y ciclo de vida de tickets (cambio de estado a `IN_PROGRESS` y `RESOLVED`).
  4. Métricas consolidadas del Dashboard para administradores.
  5. Flujo M2M completo con n8n: rechazo sin API Key (403), consulta IA con fallback RAG local, calificación por el usuario y callback webhook.

---

## 3. Supervisión Humana y Validación
- Toda la estructura de base de datos, puertos, contratos de interfaz y respuestas JSON fueron auditados contra los requerimientos técnicos y verificados en compilación local con `mvn test` y `mvn clean install` con código de salida `0` (BUILD SUCCESS).
