package com.roles.usermanagement.domain.service;

/**
 * RBAC System Roles and Granular Authorities catalog for CampusDesk.
 */
public class UserRoles {

    public enum Role {
        ADMIN,
        TECHNICIAN,
        USER
    }

    public enum Authority {
        // User and Security Permissions
        USER_READ,
        USER_CREATE,
        USER_UPDATE,
        USER_DELETE,
        ROLE_ASSIGN,
        PERMISSION_ASSIGN,
        ROLE_MANAGE,
        PERMISSION_MANAGE,

        // Ticket Permissions for Requesters (USER)
        TICKET_CREATE,
        TICKET_READ_OWN,
        TICKET_UPDATE_OWN,
        TICKET_CLOSE,

        // Technician Permissions (TECHNICIAN)
        TICKET_READ_ASSIGNED,
        TICKET_STATUS_UPDATE,

        // Administrative Permissions (ADMIN)
        TICKET_READ_ALL,
        TICKET_ASSIGN,
        REPORT_READ,

        // Common Permissions (Comments and History)
        COMMENT_CREATE,
        COMMENT_READ,
        HISTORY_READ,

        // Knowledge Base & AI Permissions
        KNOWLEDGE_READ,
        KNOWLEDGE_MANAGE,
        AI_QUERY,
        AI_MANAGE;

        public String value() {
            return name();
        }
    }
}
