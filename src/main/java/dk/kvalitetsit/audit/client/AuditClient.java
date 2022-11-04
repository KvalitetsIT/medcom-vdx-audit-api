package dk.kvalitetsit.audit.client;

import dk.kvalitetsit.audit.client.api.v1.AuditEvent;

public interface AuditClient {
    void addAuditEntry(AuditEvent<?> auditEvent);
}
