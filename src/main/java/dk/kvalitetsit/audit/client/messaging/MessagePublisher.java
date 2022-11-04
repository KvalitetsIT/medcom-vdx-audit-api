package dk.kvalitetsit.audit.client.messaging;

import io.nats.client.JetStreamApiException;

import java.io.IOException;

public interface MessagePublisher {
    void publishMessage(Object message) throws JetStreamApiException, IOException;
}
