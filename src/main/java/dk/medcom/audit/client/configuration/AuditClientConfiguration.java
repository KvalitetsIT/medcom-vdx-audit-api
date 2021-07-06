package dk.medcom.audit.client.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.medcom.audit.client.AuditClient;
import dk.medcom.audit.client.AuditClientImpl;
import dk.medcom.audit.client.actuator.NatsHealthIndicator;
import dk.medcom.audit.client.messaging.nats.NatsConnectionHandler;
import dk.medcom.audit.client.messaging.nats.NatsPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import java.io.IOException;

@Configuration
public class AuditClientConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(AuditClientConfiguration.class);
    private NatsConnectionHandler producerConnectionHandler;

    @Bean
    @ConditionalOnWebApplication
    public NatsHealthIndicator producerNatsHealthIndicator(NatsConnectionHandler natsConnectionHandler) {
        logger.info("Creating NatsHealthIndicator.");
        return new NatsHealthIndicator(natsConnectionHandler);
    }

    @Bean
    @ConditionalOnWebApplication
    public NatsConnectionHandler natsProducerConnectionHandler(@Value("${audit.nats.clusterId}") String clusterId, @Value("${audit.nats.clientId}") String clientId, @Value("${audit.nats.url}") String natsUrl) throws IOException, InterruptedException {
        logger.info("Connecting to nats at {} using client id {} and cluster id {}.", natsUrl, clientId + "-producer", clusterId);

        producerConnectionHandler = new NatsConnectionHandler(natsUrl, clusterId, clientId);
        producerConnectionHandler.connect();

        return producerConnectionHandler;
    }

    @Bean
    @ConditionalOnWebApplication
    public NatsPublisher natsPublisher(NatsConnectionHandler streamingConnection, @Value("${audit.nats.subject}") String subject) {
        logger.info("Creating NATS publisher.");
        return new NatsPublisher(streamingConnection, subject);
    }

    @Bean
    @ConditionalOnWebApplication
    public AuditClient auditClient(NatsPublisher publisher) {
        logger.info("Creating AuditClient.");
        return new AuditClientImpl(publisher);
    }

    @Bean
    @ConditionalOnMissingBean
    public AuditClient nullAuditClient() {
        return auditEvent -> {
            try {
                logger.info("Audit event received: {}", new ObjectMapper().writeValueAsString(auditEvent));
            } catch (JsonProcessingException e) {
                logger.error("Error", e);
            }
        };
    }

    @PreDestroy
    public void destroy() {
        if(producerConnectionHandler != null) {
            producerConnectionHandler.close();
        }
    }
}
