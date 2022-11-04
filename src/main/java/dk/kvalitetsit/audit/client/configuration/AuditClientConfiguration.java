package dk.kvalitetsit.audit.client.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.concurrent.TimeoutException;

@Configuration
public class AuditClientConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(AuditClientConfiguration.class);
    private Connection natsConnection;

    @Bean
    @ConditionalOnWebApplication
    @ConditionalOnProperty(name = "audit.nats.disabled", havingValue = "false", matchIfMissing = true)
    public NatsHealthIndicator auditProducerNatsHealthIndicator(Connection auditNatsProducerConnectionHandler) {
        logger.info("Creating NatsHealthIndicator.");
        return new NatsHealthIndicator(auditNatsProducerConnectionHandler);
    }

    @Bean
    @ConditionalOnWebApplication
    @ConditionalOnProperty(name = "audit.nats.disabled", havingValue = "false", matchIfMissing = true)
    public Connection natsConnection(@Value("${audit.nats.url}") String natsUrl) throws IOException, InterruptedException {
        logger.info("Connecting to nats at {} using", natsUrl);

        natsConnection = Nats.connect(natsUrl);

        return natsConnection;
    }

    @Bean
    @ConditionalOnWebApplication
    @ConditionalOnProperty(name = "audit.nats.disabled", havingValue = "false", matchIfMissing = true)
    public NatsPublisher auditNatsPublisher(Connection natsConnection, @Value("${audit.nats.subject}") String subject) throws IOException {
        logger.info("Creating NATS publisher.");
        return new NatsPublisher(natsConnection.jetStream(), subject);
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
                logger.debug("Audit event received. Not sending to NATS. Event: {}", new ObjectMapper().writeValueAsString(auditEvent));
            } catch (JsonProcessingException e) {
                logger.error("Error", e);
            }
        };
    }

    @PreDestroy
    public void destroy() throws InterruptedException, TimeoutException {
        if(natsConnection != null) {
            natsConnection.drain(Duration.ofSeconds(5L));
        }
    }
}
