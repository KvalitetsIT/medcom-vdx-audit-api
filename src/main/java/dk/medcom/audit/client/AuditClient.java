package dk.medcom.audit.client;

import dk.medcom.audit.client.api.v1.AuditEvent;

public interface AuditClient {
    void addAuditEntry(AuditEvent<?> auditEvent);
}
