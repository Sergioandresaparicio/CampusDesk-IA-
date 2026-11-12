package com.roles.usermanagement.core.seeder;

import com.roles.usermanagement.domain.service.SecurityBootstrapService;
import com.roles.usermanagement.domain.service.UserRoles;
import com.roles.usermanagement.modules.ai.entity.AiQuery;
import com.roles.usermanagement.modules.ai.entity.AiRating;
import com.roles.usermanagement.modules.ai.entity.AiResponse;
import com.roles.usermanagement.modules.ai.entity.AiResponseSource;
import com.roles.usermanagement.modules.ai.enums.AiQueryStatus;
import com.roles.usermanagement.modules.ai.repository.AiQueryRepository;
import com.roles.usermanagement.modules.ai.repository.AiRatingRepository;
import com.roles.usermanagement.modules.ai.repository.AiResponseRepository;
import com.roles.usermanagement.modules.knowledgebase.entity.KnowledgeArticle;
import com.roles.usermanagement.modules.knowledgebase.repository.KnowledgeArticleRepository;
import com.roles.usermanagement.modules.technician.entity.TechnicianProfile;
import com.roles.usermanagement.modules.technician.enums.TechnicianAvailabilityStatus;
import com.roles.usermanagement.modules.technician.repository.TechnicianProfileRepository;
import com.roles.usermanagement.modules.ticket.entity.Ticket;
import com.roles.usermanagement.modules.ticket.enums.TicketCategory;
import com.roles.usermanagement.modules.ticket.enums.TicketPriority;
import com.roles.usermanagement.modules.ticket.enums.TicketStatus;
import com.roles.usermanagement.modules.ticket.repository.TicketRepository;
import com.roles.usermanagement.persistance.crud.UserCrudRepository;
import com.roles.usermanagement.persistance.crud.UserRoleCrudRepository;
import com.roles.usermanagement.persistance.entity.UserEntity;
import com.roles.usermanagement.persistance.entity.UserRoleEntity;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Seeds rich baseline domain data for technicians, users, verified knowledge base articles,
 * and realistic historical incidents for immediate analytics dashboard verification (RF06, RF07, RF09).
 */
@Component
@Order(2)
public class DataSeeder implements ApplicationRunner {

    private final SecurityBootstrapService bootstrapService;
    private final UserCrudRepository userRepository;
    private final UserRoleCrudRepository userRoleRepository;
    private final TechnicianProfileRepository technicianRepository;
    private final KnowledgeArticleRepository articleRepository;
    private final TicketRepository ticketRepository;
    private final AiQueryRepository queryRepository;
    private final AiResponseRepository responseRepository;
    private final AiRatingRepository ratingRepository;
    private final PasswordEncoder encoder;

    public DataSeeder(SecurityBootstrapService bootstrapService,
                      UserCrudRepository userRepository,
                      UserRoleCrudRepository userRoleRepository,
                      TechnicianProfileRepository technicianRepository,
                      KnowledgeArticleRepository articleRepository,
                      TicketRepository ticketRepository,
                      AiQueryRepository queryRepository,
                      AiResponseRepository responseRepository,
                      AiRatingRepository ratingRepository,
                      PasswordEncoder encoder) {
        this.bootstrapService = bootstrapService;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.technicianRepository = technicianRepository;
        this.articleRepository = articleRepository;
        this.ticketRepository = ticketRepository;
        this.queryRepository = queryRepository;
        this.responseRepository = responseRepository;
        this.ratingRepository = ratingRepository;
        this.encoder = encoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        bootstrapService.initialize();
        seedUsersAndTechnicians();
        seedKnowledgeBase();
        seedHistoricalTickets();
    }

    private void seedUsersAndTechnicians() {
        // Technician 1: Redes
        createStaffUser("tecnico_redes", "tecnico.redes@technova.com", "Tecnico123!", UserRoles.Role.TECHNICIAN.name());
        if (technicianRepository.findByUsername("tecnico_redes").isEmpty()) {
            technicianRepository.save(TechnicianProfile.builder()
                    .username("tecnico_redes")
                    .specialty("Infraestructura y Conectividad")
                    .availabilityStatus(TechnicianAvailabilityStatus.DISPONIBLE)
                    .activeTicketsCount(1)
                    .maxConcurrentTickets(5)
                    .build());
        }

        // Technician 2: Hardware
        createStaffUser("tecnico_hardware", "tecnico.hardware@technova.com", "Tecnico123!", UserRoles.Role.TECHNICIAN.name());
        if (technicianRepository.findByUsername("tecnico_hardware").isEmpty()) {
            technicianRepository.save(TechnicianProfile.builder()
                    .username("tecnico_hardware")
                    .specialty("Mantenimiento Físico y Periféricos")
                    .availabilityStatus(TechnicianAvailabilityStatus.DISPONIBLE)
                    .activeTicketsCount(2)
                    .maxConcurrentTickets(5)
                    .build());
        }

        // Technician 3: Software
        createStaffUser("tecnico_software", "tecnico.software@technova.com", "Tecnico123!", UserRoles.Role.TECHNICIAN.name());
        if (technicianRepository.findByUsername("tecnico_software").isEmpty()) {
            technicianRepository.save(TechnicianProfile.builder()
                    .username("tecnico_software")
                    .specialty("Sistemas Operativos y Plataformas")
                    .availabilityStatus(TechnicianAvailabilityStatus.DISPONIBLE)
                    .activeTicketsCount(0)
                    .maxConcurrentTickets(5)
                    .build());
        }

        // Regular Requester User
        createStaffUser("usuario_campus", "usuario@technova.com", "Usuario123!", UserRoles.Role.USER.name());
    }

    private void createStaffUser(String username, String email, String password, String role) {
        if (!userRepository.existsById(username)) {
            UserEntity user = new UserEntity();
            user.setUsername(username);
            user.setEmail(email.trim().toLowerCase());
            user.setPassword(encoder.encode(password));
            user.setLocked(false);
            user.setDisabled(false);
            user.setMustChangePassword(false);
            user.setRoles(new ArrayList<>());
            userRepository.save(user);

            UserRoleEntity assignment = new UserRoleEntity();
            assignment.setUsername(username);
            assignment.setRole(role);
            userRoleRepository.save(assignment);
        }
    }

    private void seedKnowledgeBase() {
        if (articleRepository.count() > 0) return;

        List<KnowledgeArticle> articles = List.of(
                KnowledgeArticle.builder()
                        .title("Resolución de falla de conectividad WiFi en aulas y laboratorios")
                        .category(TicketCategory.REDES)
                        .problem("Los equipos portátiles pierden la conexión inalámbrica de forma intermitente o no obtienen dirección IP.")
                        .symptoms("Icono de red con advertencia amarilla, mensaje de 'Sin Internet, segura', timeout al hacer ping a 8.8.8.8.")
                        .cause("Saturación de la banda 2.4GHz en el punto de acceso AP o tabla DHCP agotada en la VLAN correspondiente.")
                        .solution("1. Reiniciar el punto de acceso Cisco/Ubiquiti asignado a la zona. 2. Forzar conexión por la banda 5GHz en el cliente. 3. Limpiar leases DHCP expirados en el controlador central de red.")
                        .active(true)
                        .verified(true)
                        .embeddingId("qdrant-vec-redes-001")
                        .createdAt(LocalDateTime.now().minusDays(30))
                        .build(),

                KnowledgeArticle.builder()
                        .title("Procedimiento ante error de concesión DHCP en switches de acceso")
                        .category(TicketCategory.REDES)
                        .problem("Dispositivos conectados por cable Ethernet muestran IP del rango APIPA 169.254.x.x.")
                        .symptoms("Imposibilidad de navegar por Intranet y falta de respuesta a peticiones DHCP Discover.")
                        .cause("DHCP Snooping bloqueando puertos no confiables o servicio DHCP corporativo sin disponibilidad.")
                        .solution("Verificar la configuración de 'ip dhcp snooping trust' en el puerto uplink del switch y reiniciar el daemon DHCP.")
                        .active(true)
                        .verified(true)
                        .embeddingId("qdrant-vec-redes-002")
                        .createdAt(LocalDateTime.now().minusDays(25))
                        .build(),

                KnowledgeArticle.builder()
                        .title("Impresora de red no responde o trabajos quedan en cola pausada")
                        .category(TicketCategory.HARDWARE)
                        .problem("La impresora departamental multifuncional figura sin conexión desde las estaciones de trabajo.")
                        .symptoms("Cola de impresión con estado 'Error - Imprimiendo', luz ámbar en panel de impresora.")
                        .cause("Conflicto de IP asignada por DHCP en impresora o atasco en el carrete de impresión (spooler) de Windows.")
                        .solution("1. Reiniciar el servicio 'Spooler de impresión' mediante net stop spooler / net start spooler. 2. Asignar IP estática reservada a la impresora en la consola del router.")
                        .active(true)
                        .verified(true)
                        .embeddingId("qdrant-vec-hw-001")
                        .createdAt(LocalDateTime.now().minusDays(20))
                        .build(),

                KnowledgeArticle.builder()
                        .title("Desbloqueo y restablecimiento de cuenta institucional de usuario")
                        .category(TicketCategory.ACCESOS)
                        .problem("El usuario no puede ingresar al portal institucional ni al correo por bloqueo de seguridad.")
                        .symptoms("Mensaje 'Su cuenta ha sido bloqueada temporalmente por múltiples intentos fallidos'.")
                        .cause("Tres intentos consecutivos con contraseña incorrecta en el portal SSO.")
                        .solution("Verificar la identidad del solicitante vía correo alternativo o documento oficial, desbloquear la cuenta desde el panel de administración (/api/user/update con locked=false) y generar clave temporal.")
                        .active(true)
                        .verified(true)
                        .embeddingId("qdrant-vec-acc-001")
                        .createdAt(LocalDateTime.now().minusDays(15))
                        .build(),

                KnowledgeArticle.builder()
                        .title("Fallo de ejecución o cierre inesperado en software académico de diseño")
                        .category(TicketCategory.SOFTWARE)
                        .problem("Aplicaciones de modelado o simulación se cierran al momento de renderizar o iniciar.")
                        .symptoms("Ventana emergente 'Application terminated unexpectedly - EXCEPTION_ACCESS_VIOLATION'.")
                        .cause("Controlador de tarjeta de video desactualizado o librerías de Microsoft Visual C++ Redistributable corruptas.")
                        .solution("Reinstalar el paquete 'Visual C++ Redistributable AIO' y actualizar el controlador NVIDIA/Intel a la versión certificada.")
                        .active(true)
                        .verified(true)
                        .embeddingId("qdrant-vec-sw-001")
                        .createdAt(LocalDateTime.now().minusDays(10))
                        .build()
        );

        articleRepository.saveAll(articles);
    }

    private void seedHistoricalTickets() {
        if (ticketRepository.count() > 0) return;

        LocalDateTime now = LocalDateTime.now();
        List<Ticket> sampleTickets = List.of(
                Ticket.builder()
                        .title("Sin acceso a WiFi en Sala de Profesores")
                        .description("La red inalámbrica 'Campus-Docentes' no conecta a los portátiles.")
                        .category(TicketCategory.REDES)
                        .priority(TicketPriority.ALTA)
                        .status(TicketStatus.OPEN)
                        .applicantUsername("usuario_campus")
                        .technicianUsername(null)
                        .room("Edificio Docencia - Sala 102")
                        .createdAt(now.minusHours(3))
                        .build(),

                Ticket.builder()
                        .title("Falla de impresora en Departamento de Registro")
                        .description("La impresora HP LaserJet departamental retiene los documentos en cola y no imprime.")
                        .category(TicketCategory.HARDWARE)
                        .priority(TicketPriority.MEDIA)
                        .status(TicketStatus.ASSIGNED)
                        .applicantUsername("usuario_campus")
                        .technicianUsername("tecnico_hardware")
                        .device("Impresora HP LaserJet M404n")
                        .room("Oficina de Registro")
                        .createdAt(now.minusHours(6))
                        .assignedAt(now.minusHours(5))
                        .build(),

                Ticket.builder()
                        .title("Error de concesión DHCP en Laboratorio de Computación 4")
                        .description("Cinco máquinas obtienen dirección 169.254.x.x tras encenderse esta mañana.")
                        .category(TicketCategory.REDES)
                        .priority(TicketPriority.CRITICA)
                        .status(TicketStatus.IN_PROGRESS)
                        .applicantUsername("usuario_campus")
                        .technicianUsername("tecnico_redes")
                        .room("Laboratorio 4 - Torre Este")
                        .createdAt(now.minusHours(12))
                        .assignedAt(now.minusHours(11))
                        .build(),

                Ticket.builder()
                        .title("Bloqueo de acceso al Sistema de Notas")
                        .description("Docente solicita desbloqueo de cuenta tras olvidar contraseña temporal.")
                        .category(TicketCategory.ACCESOS)
                        .priority(TicketPriority.MEDIA)
                        .status(TicketStatus.RESOLVED)
                        .applicantUsername("usuario_campus")
                        .technicianUsername("tecnico_software")
                        .resolutionNotes("Se validó identidad institucional y se generó enlace de autoservicio de contraseña.")
                        .createdAt(now.minusDays(2))
                        .assignedAt(now.minusDays(2).plusMinutes(15))
                        .resolvedAt(now.minusDays(2).plusMinutes(45))
                        .build(),

                Ticket.builder()
                        .title("Cierre inesperado en simulador de electrónica")
                        .description("El software Proteus se cierra al cargar circuitos complejos en el Aula Digital.")
                        .category(TicketCategory.SOFTWARE)
                        .priority(TicketPriority.BAJA)
                        .status(TicketStatus.CLOSED)
                        .applicantUsername("usuario_campus")
                        .technicianUsername("tecnico_software")
                        .resolutionNotes("Se actualizaron librerías C++ y controladores de gráficos en todas las estaciones.")
                        .createdAt(now.minusDays(4))
                        .assignedAt(now.minusDays(4).plusHours(1))
                        .resolvedAt(now.minusDays(4).plusHours(3))
                        .closedAt(now.minusDays(3))
                        .build(),

                Ticket.builder()
                        .title("Proyector multimedia no emite señal HDMI")
                        .description("El proyector del Auditorio Principal no reconoce la entrada HDMI del atril.")
                        .category(TicketCategory.HARDWARE)
                        .priority(TicketPriority.ALTA)
                        .status(TicketStatus.RESOLVED)
                        .applicantUsername("usuario_campus")
                        .technicianUsername("tecnico_hardware")
                        .resolutionNotes("Se sustituyó el cable HDMI defectuoso del atril y se ajustó resolución nativa a 1080p.")
                        .createdAt(now.minusDays(5))
                        .assignedAt(now.minusDays(5).plusMinutes(30))
                        .resolvedAt(now.minusDays(5).plusHours(2))
                        .build()
        );

        ticketRepository.saveAll(sampleTickets);

        // Seed 1 AI Query with rating
        Ticket firstTicket = sampleTickets.get(0);
        AiQuery query = AiQuery.builder()
                .ticketId(firstTicket.getId())
                .question("¿Cómo resolver desconexión intermitente de WiFi en aulas?")
                .status(AiQueryStatus.RESPONDIDA)
                .username("usuario_campus")
                .createdAt(now.minusHours(2))
                .build();
        AiQuery savedQuery = queryRepository.save(query);

        AiResponse aiResponse = AiResponse.builder()
                .query(savedQuery)
                .responseText("Solución recomendada: Reiniciar el punto de acceso Cisco/Ubiquiti y limpiar leases DHCP expirados.")
                .suggestedSteps("1. Reiniciar AP. 2. Forzar banda 5GHz. 3. Limpiar tabla DHCP.")
                .modelUsed("campusdesk-rag-engine-v1")
                .confidenceScore(0.92)
                .createdAt(now.minusHours(2))
                .sources(new ArrayList<>())
                .build();
        AiResponse savedResponse = responseRepository.save(aiResponse);

        articleRepository.findAll().stream().findFirst().ifPresent(article -> {
            AiResponseSource source = AiResponseSource.builder()
                    .response(savedResponse)
                    .article(article)
                    .similarityScore(0.92)
                    .build();
            savedResponse.getSources().add(source);
            responseRepository.save(savedResponse);
        });

        AiRating rating = AiRating.builder()
                .response(savedResponse)
                .score(5)
                .comment("Excelente recomendación técnica, solucionó la falla en 10 minutos.")
                .ratedBy("usuario_campus")
                .createdAt(now.minusHours(1))
                .build();
        ratingRepository.save(rating);
    }
}
