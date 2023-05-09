package dk.kvalitetsit.audit.client;

import dk.kvalitetsit.audit.client.api.v1.AuditEvent;

public interface AuditClient {
    /**
     * Add audit entry.
     * <p>
     * Depending on configuration the entry will be published to nats or logged through slf4j.
     * @param auditEvent The event to add.
     */
    void addAuditEntry(AuditEvent<?> auditEvent);
}
