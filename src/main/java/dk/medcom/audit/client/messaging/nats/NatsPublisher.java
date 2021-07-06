package dk.medcom.audit.client.messaging.nats;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dk.medcom.audit.client.messaging.MessagePublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class NatsPublisher implements MessagePublisher {
    private static final Logger logger = LoggerFactory.getLogger(NatsPublisher.class);

    private final NatsConnectionHandler connectionHandler;
    private final String topic;

    public NatsPublisher(NatsConnectionHandler connectionHandler, String subject) {
        this.connectionHandler = connectionHandler;
        this.topic = subject;
    }

    public void publishMessage(Object message) throws IOException, InterruptedException, TimeoutException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
        String jsonResult = mapper.writeValueAsString(message);

        connectionHandler.getConnection().publish(topic, jsonResult.getBytes(StandardCharsets.UTF_8));
        logger.debug("Event published to {} with content: {}", topic, jsonResult);
    }
 }
