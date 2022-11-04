package dk.kvalitetsit.audit.client;

public class NatsAuditEvent<T> {
    private int version;
    private T auditEvent;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public T getAuditEvent() {
        return auditEvent;
    }

    public void setAuditEvent(T auditEvent) {
        this.auditEvent = auditEvent;
    }
}
