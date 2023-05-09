package dk.kvalitetsit.audit.client.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dk.kvalitetsit.audit.client.AuditClient;
import dk.kvalitetsit.audit.client.AuditClientImpl;
import dk.kvalitetsit.audit.client.actuator.NatsHealthIndicator;
import dk.kvalitetsit.audit.client.messaging.nats.NatsPublisher;
import io.nats.client.Connection;
import io.nats.client.Nats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.time.Duration;

@Configuration
public class AuditClientConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(AuditClientConfiguration.class);
    private Connection auditNatsConnection;

    @Bean
    @ConditionalOnWebApplication
    @ConditionalOnProperty(name = "audit.nats.disabled", havingValue = "false", matchIfMissing = true)
    public NatsHealthIndicator auditProducerNatsHealthIndicator(Connection auditNatsConnection) {
        logger.info("Creating NatsHealthIndicator.");
        return new NatsHealthIndicator(auditNatsConnection);
    }

    @Bean(initMethod = "")
    @ConditionalOnWebApplication
    @ConditionalOnProperty(name = "audit.nats.disabled", havingValue = "false", matchIfMissing = true)
    public Connection auditNatsConnection(@Value("${audit.nats.url}") String natsUrl) throws IOException, InterruptedException {
        logger.info("Connecting to nats at {} using", natsUrl);

        auditNatsConnection = Nats.connect(natsUrl);

        return auditNatsConnection;
    }

    @Bean
    @ConditionalOnWebApplication
    @ConditionalOnProperty(name = "audit.nats.disabled", havingValue = "false", matchIfMissing = true)
    public NatsPublisher auditNatsPublisher(Connection auditNatsConnection, @Value("${audit.nats.subject}") String subject) throws IOException {
        logger.info("Creating NATS publisher.");
        return new NatsPublisher(auditNatsConnection.jetStream(), subject);
    }

    @Bean
    @ConditionalOnWebApplication
    @ConditionalOnProperty(name = "audit.nats.disabled", havingValue = "false", matchIfMissing = true)
    public AuditClient auditClient(NatsPublisher auditNatsPublisher) {
        logger.info("Creating AuditClient.");
        return new AuditClientImpl(auditNatsPublisher);
    }

    @Bean
    @ConditionalOnMissingBean
    public AuditClient nullAuditClient() {
        return auditEvent -> {
            try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                mapper.disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
                logger.debug("Audit event received. Not sending to NATS. Event: {}", mapper.writeValueAsString(auditEvent));
            } catch (JsonProcessingException e) {
                logger.error("Error", e);
            }
        };
    }

    @PreDestroy
    public void destroy() throws Exception {
        if(auditNatsConnection != null) {
            auditNatsConnection.drain(Duration.ofSeconds(5L));
        }
    }
}
