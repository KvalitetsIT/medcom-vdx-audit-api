package dk.kvalitetsit.audit.client.messaging.nats;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dk.kvalitetsit.audit.client.messaging.MessagePublisher;
import io.nats.client.JetStream;
import io.nats.client.JetStreamApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class NatsPublisher implements MessagePublisher {
    private static final Logger logger = LoggerFactory.getLogger(NatsPublisher.class);

    private final JetStream jetStream;
    private final String subject;

    public NatsPublisher(JetStream jetStream, String subject) {
        this.jetStream = jetStream;
        this.subject = subject;
    }

    public void publishMessage(Object message) throws JetStreamApiException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
        String jsonResult = mapper.writeValueAsString(message);

        jetStream.publish(subject, jsonResult.getBytes(StandardCharsets.UTF_8));
        logger.debug("Event published to {} with content: {}", subject, jsonResult);
    }
 }
