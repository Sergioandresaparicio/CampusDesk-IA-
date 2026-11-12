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

---

## 4. Infraestructura Docker y Orquestación n8n (Rama `Dev-Diego`)

### 4.1 Herramientas y Alcance
- En la fase de infraestructura y automatización inteligente (`Dev-Diego`), se utilizó **Antigravity AI** para estructurar la contenerización con Docker Compose y diseñar los dos flujos obligatorios de **n8n** integrados con **Qdrant**, **OpenRouter** y los endpoints M2M de Spring Boot.

### 4.2 Componentes Asistidos y Decisiones Técnicas
- **Contenerización (`Dockerfile`, `docker-compose.yml`, `.env.example`):**
  - Se creó un `Dockerfile` multi-stage (`maven:3.9.9-eclipse-temurin-21-alpine` → `eclipse-temurin:21-jre-alpine`) para compilar y ejecutar el backend sin depender de Java 21/Maven locales.
  - Se configuró `docker-compose.yml` con `postgres:16-alpine`, `qdrant/qdrant:latest`, `docker.n8n.io/n8nio/n8n:latest` y `backend`, incluyendo `healthcheck` nativo sin `curl` para Qdrant (`/dev/tcp/127.0.0.1/6333`), `/healthz` para n8n, `N8N_BLOCK_ENV_ACCESS_IN_NODE=false` para lectura de variables `.env`, y `extra_hosts` (`host.docker.internal`) para soportar ejecución híbrida.
  - Se actualizó `.gitignore` para excluir `.env` y volúmenes locales de datos.
- **Flujo 1 — Indexación en Qdrant ([01-knowledge-base-indexing.json](n8n/workflows/01-knowledge-base-indexing.json)):**
  - Creación idempotente de colección en Qdrant (`GET` con fallback `PUT`), consulta autenticada (`X-N8N-Api-Key`) a `/api/v1/integrations/n8n/data`, generación de embeddings vía OpenRouter (`/api/v1/embeddings`), *upsert* usando `article.id` como identificador de punto para evitar duplicados, y depuración automática (`scroll` + `delete`) de artículos desactivados.
- **Flujo 2 — Asistencia RAG ([02-rag-incident-assistant.json](n8n/workflows/02-rag-incident-assistant.json)):**
  - Recepción en `POST /webhook/campusdesk-incident`, validación de texto no vacío (`400 Bad Request` en entradas vacías, `202 Accepted` inmediato para evitar timeout de 15s en `RestClient`), búsqueda vectorial Top-5 en Qdrant con filtro `active=true && verified=true` y `score_threshold` configurable (`0.70`), bifurcación estricta a `SIN_RESULTADOS` cuando no hay fuentes sobre el umbral, generación fundamentada en JSON vía OpenRouter LLM, y manejo de errores por salida secundaria (`continueErrorOutput`) reportando `status: "ERROR"` al callback `/api/v1/integrations/n8n/callback`.

### 4.3 Validación Pendiente en Entorno Local
- Los archivos JSON de n8n fueron validados sintácticamente tras su generación; la ejecución completa de contenedores (`docker compose up`) y pruebas extremo a extremo con API Key real de OpenRouter quedan pendientes de instalación de Docker Desktop y WSL 2 en el equipo local.
