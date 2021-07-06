package dk.medcom.audit.client.messaging;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface MessagePublisher {
    void publishMessage(Object message) throws IOException, InterruptedException, TimeoutException;
}
