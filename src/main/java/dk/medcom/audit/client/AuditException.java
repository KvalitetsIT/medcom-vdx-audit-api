package dk.medcom.audit.client;

public class AuditException extends RuntimeException {
    public AuditException(String message, Exception e) {
        super(message, e);
    }
}
