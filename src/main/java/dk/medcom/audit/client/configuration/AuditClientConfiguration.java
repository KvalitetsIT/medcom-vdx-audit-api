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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
    @ConditionalOnProperty(name = "audit.nats.disabled", havingValue = "false", matchIfMissing = true)
    public NatsHealthIndicator auditProducerNatsHealthIndicator(NatsConnectionHandler auditNatsProducerConnectionHandler) {
        logger.info("Creating NatsHealthIndicator.");
        return new NatsHealthIndicator(auditNatsProducerConnectionHandler);
    }

    @Bean
    @ConditionalOnWebApplication
    @ConditionalOnProperty(name = "audit.nats.disabled", havingValue = "false", matchIfMissing = true)
    public NatsConnectionHandler auditNatsProducerConnectionHandler(@Value("${audit.nats.cluster.id}") String clusterId, @Value("${audit.nats.client.id}") String clientId, @Value("${audit.nats.url}") String natsUrl) throws IOException, InterruptedException {
        logger.info("Connecting to nats at {} using client id {} and cluster id {}.", natsUrl, clientId + "-audit-producer", clusterId);

        producerConnectionHandler = new NatsConnectionHandler(natsUrl, clusterId, clientId + "-audit-producer");
        producerConnectionHandler.connect();

        return producerConnectionHandler;
    }

    @Bean
    @ConditionalOnWebApplication
    @ConditionalOnProperty(name = "audit.nats.disabled", havingValue = "false", matchIfMissing = true)
    public NatsPublisher auditNatsPublisher(NatsConnectionHandler auditNatsProducerConnectionHandler, @Value("${audit.nats.subject}") String subject) {
        logger.info("Creating NATS publisher.");
        return new NatsPublisher(auditNatsProducerConnectionHandler, subject);
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
    public void destroy() {
        if(producerConnectionHandler != null) {
            producerConnectionHandler.close();
        }
    }
}
