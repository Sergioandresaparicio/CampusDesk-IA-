# CampusDesk IA — Infraestructura Docker y Orquestación n8n

Este directorio contiene los flujos de trabajo exportados de **n8n** para la indexación semántica en **Qdrant** y la asistencia inteligente **RAG** con **OpenRouter**, integrados mediante autenticación M2M (`X-N8N-Api-Key`) con el backend en Spring Boot.

---

## 1. Requisitos e Instalación en Windows (PowerShell como Administrador)

Si aún no tienes **Docker Desktop** ni **WSL 2** instalados en tu equipo, abre **PowerShell como Administrador** y ejecuta:

```powershell
wsl --install
winget install Docker.DockerDesktop
```

> Reinicia el equipo si WSL lo solicita, abre **Docker Desktop**, acepta los términos y espera a que el motor aparezca en estado *Running*.

---

## 2. Levantar la Arquitectura Completa con Docker Compose

1. Crea tu archivo `.env` a partir de la plantilla:
   ```powershell
   Copy-Item .env.example .env
   ```
2. Edita `.env` y coloca tu clave real en `OPENROUTER_API_KEY` (y ajusta contraseñas si lo deseas).
3. Levanta PostgreSQL, Qdrant, n8n y el backend Spring Boot:
   ```powershell
   docker compose up -d --build
   ```
4. Verifica el estado de los contenedores:
   ```powershell
   docker compose ps
   ```

### Puertos y URLs locales
- **Backend Spring Boot (Swagger UI):** `http://localhost:8051/swagger-ui.html`
- **n8n Editor UI:** `http://localhost:5678`
- **Qdrant Dashboard / API:** `http://localhost:6333/dashboard`
- **PostgreSQL:** `localhost:5432`

> **Nota si ejecutas Spring Boot fuera de Docker (`mvn spring-boot:run`):**
> Levanta solo la infraestructura (`docker compose up -d postgres qdrant n8n`) y cambia en `.env`:
> `BACKEND_BASE_URL=http://host.docker.internal:8051`

---

## 3. Importar y Activar los Flujos de n8n

Los workflows están montados en modo lectura dentro del contenedor de n8n en `/home/node/workflows` y también disponibles en el repositorio:

- [01-knowledge-base-indexing.json](workflows/01-knowledge-base-indexing.json)
- [02-rag-incident-assistant.json](workflows/02-rag-incident-assistant.json)

### Opción A — Desde la interfaz web de n8n (`http://localhost:5678`)
1. Crea tu cuenta local inicial en n8n.
2. Ve a **Workflows → Add Workflow → Import from File** e importa ambos archivos `.json`.
3. **Activa (Publish / Toggle Active)** ambos workflows (especialmente `02-rag-incident-assistant.json` para habilitar el webhook productivo `POST /webhook/campusdesk-incident`).
4. Ejecuta manualmente una vez el flujo `01-knowledge-base-indexing` para poblar Qdrant con los artículos activos y verificados iniciales del `DataSeeder`.

### Opción B — Importación automática por CLI en Docker
```powershell
docker exec campusdesk-n8n n8n import:workflow --input=/home/node/workflows/01-knowledge-base-indexing.json
docker exec campusdesk-n8n n8n import:workflow --input=/home/node/workflows/02-rag-incident-assistant.json
```
*(Luego actívalos desde la UI de n8n en `http://localhost:5678`.)*

---

## 4. Justificación del Umbral de Similitud (`QDRANT_SCORE_THRESHOLD=0.70`)

- La colección `campusdesk_kb` utiliza similitud **Cosine** con embeddings de 1536 dimensiones (`openai/text-embedding-3-small`).
- En pruebas de RAG sobre descripciones técnicas en español (donde los síntomas de usuario suelen variar léxicamente respecto al título técnico del artículo), un umbral de **`0.70`**:
  - Permite recuperar entre **3 y 5 artículos** realmente relacionados con el dominio del incidente (WiFi, DHCP, impresoras, accesos, crashes).
  - Filtra consultas fuera de dominio o sin respaldo documental suficiente (en cuyo caso n8n evita llamar al LLM y reporta `SIN_RESULTADOS` vía callback).
- El parámetro es 100% configurable mediante la variable `QDRANT_SCORE_THRESHOLD` en `.env`.

---

## 5. Detalles de Sincronización Backend ↔ n8n

1. **Autenticación M2M**: Todos los requests de n8n hacia Spring Boot envían `X-N8N-Api-Key: <N8N_API_KEY>` (validado por `N8nApiKeyFilter`).
2. **Artículos desactivados**: El Flujo 1 consulta `GET /api/v1/integrations/n8n/data` (que solo devuelve artículos con `active = true` y `verified = true`), realiza *upsert* usando el `article.id` numérico como ID de punto en Qdrant (sin duplicados) y ejecuta `points/delete` sobre cualquier ID que haya dejado de estar activo. Además, `N8nIntegrationService.processCallback` valida en base relacional que cada fuente siga activa antes de guardarla.
3. **Resiliencia ante fallos**: El Flujo 2 captura errores en OpenRouter Embeddings, Qdrant Search y OpenRouter Chat (`onError: continueErrorOutput`), enviando un callback con `status: "ERROR"` para que `AiQuery` registre el fallo en BD.
4. **Nota sobre `trigger` + fallback local**: Actualmente `N8nIntegrationService.triggerN8n(...)` responde síncronamente con el motor heurístico local después de despachar el webhook a n8n, y segundos después el callback de n8n actualiza esa `AiQuery` / `AiResponse` en base de datos con la respuesta generada por OpenRouter + Qdrant. Consulta `GET /api/v1/integrations/n8n/queries/ticket/{ticketId}` tras ~3 segundos para ver la respuesta final persistida por n8n.
