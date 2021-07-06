package dk.medcom.audit.client;

import dk.medcom.audit.client.api.v1.AuditEvent;
import dk.medcom.audit.client.messaging.nats.NatsPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class AuditClientImpl implements AuditClient {
    private static final Logger logger = LoggerFactory.getLogger(AuditClientImpl.class);
    private final NatsPublisher publisher;

    public AuditClientImpl(NatsPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void addAuditEntry(AuditEvent<?> auditEvent) {
        logger.debug("Sending audit event");
        new AuditEventValidator().validate(auditEvent);

        var natsEvent = new NatsAuditEvent<AuditEvent<?>>();
        natsEvent.setVersion(1);
        natsEvent.setAuditEvent(auditEvent);

        try {
            publisher.publishMessage(natsEvent);
        }
        catch (IOException | InterruptedException | TimeoutException e) {
            logger.error("Error publishing audit event.", e);
            throw new AuditException("Error publishing audit event.", e);
        }
    }
}
