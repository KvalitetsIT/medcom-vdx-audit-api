package dk.kvalitetsit.audit.client.api.v1;

import java.time.OffsetDateTime;

public class AuditEvent<T> {
    private String organisationCode;
    private String resource;
    private String operation;
    private OffsetDateTime auditEventDateTime;
    private String source;
    private String identifier;
    private String user;
    private T auditData;

    public T getAuditData() {
        return auditData;
    }

    public void setAuditData(T auditData) {
        this.auditData = auditData;
    }

    public String getOrganisationCode() {
        return organisationCode;
    }

    public void setOrganisationCode(String organisationCode) {
        this.organisationCode = organisationCode;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public OffsetDateTime getAuditEventDateTime() {
        return auditEventDateTime;
    }

    public void setAuditEventDateTime(OffsetDateTime auditEventDateTime) {
        this.auditEventDateTime = auditEventDateTime;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }
}
