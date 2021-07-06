package dk.medcom.audit.client.messaging.nats;

import io.nats.client.Connection;
import io.nats.client.Consumer;
import io.nats.client.ErrorListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NatsErrorListener implements ErrorListener {
    private static final Logger logger = LoggerFactory.getLogger(NatsErrorListener.class);

    @Override
    public void errorOccurred(Connection connection, String s) {
        logger.error("A error occurred. Connection status: '{}'. Error msg: '{}'", connection.getStatus(), s);
    }

    @Override
    public void exceptionOccurred(Connection connection, Exception e) {
        logger.error(String.format("A exception occurred. Connection status: '%s'.", connection.getStatus()), e);
    }

    @Override
    public void slowConsumerDetected(Connection connection, Consumer consumer) {
        logger.error(String.format("A slow consumer detected. Connection status: '%s'. Consumer info: '%s'", connection.getStatus(), consumer.toString()));
    }
}
