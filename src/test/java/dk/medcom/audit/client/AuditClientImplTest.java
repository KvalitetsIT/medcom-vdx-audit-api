package dk.medcom.audit.client;

import dk.medcom.audit.client.api.v1.AuditEvent;
import dk.medcom.audit.client.messaging.nats.NatsPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

public class AuditClientImplTest{
    private AuditClientImpl auditClient;
    private NatsPublisher eventPublisher;

    @BeforeEach
    public void setup() {
        eventPublisher = Mockito.mock(NatsPublisher.class);
        auditClient = new AuditClientImpl(eventPublisher);
    }

    @Test
    public void testAuditEvent() throws IOException, InterruptedException, TimeoutException {
        var input = new AuditEvent<String>();
        input.setAuditEventDateTime(OffsetDateTime.now());
        input.setIdentifier("id");
        input.setOperation("operation");
        input.setSource("source");
        input.setUser("user");
        input.setAuditData("audit data");
        input.setOrganisationCode("organisation code");

        auditClient.addAuditEntry(input);

        var natsAuditEventCaptor = ArgumentCaptor.forClass(NatsAuditEvent.class);
        Mockito.verify(eventPublisher, times(1)).publishMessage(natsAuditEventCaptor.capture());

        assertNotNull(natsAuditEventCaptor.getValue());
        var natsAuditEvent = natsAuditEventCaptor.getValue();
        assertEquals(1, natsAuditEvent.getVersion());
        assertEquals(input, natsAuditEvent.getAuditEvent());
    }

    @Test
    public void testAuditEventValidationError() throws IOException, InterruptedException, TimeoutException {
        var input = new AuditEvent<String>();

        assertThrows(AuditValidationException.class, () -> auditClient.addAuditEntry(input));
        Mockito.verify(eventPublisher, never()).publishMessage(Mockito.any());
    }
}
