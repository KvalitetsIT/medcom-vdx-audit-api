package dk.medcom.audit.client.actuator;

import dk.medcom.audit.client.messaging.nats.NatsConnectionHandler;
import io.nats.client.Connection;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;

public class NatsHealthIndicator extends AbstractHealthIndicator {
    private final NatsConnectionHandler natsConnectionHandler;

    public NatsHealthIndicator(NatsConnectionHandler natsConnectionHandler){
        this.natsConnectionHandler = natsConnectionHandler;
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) {
        Connection.Status status = natsConnectionHandler.getStatus();
        if (status == Connection.Status.CONNECTED){
            builder.up()
                    .withDetail("url", natsConnectionHandler.getConnectedUrl())
                    .withDetail("status", status.toString());
        }
        else {
            builder.down()
                    .withDetail("url", natsConnectionHandler.getConnectedUrl())
                    .withDetail("status", status.toString());
        }
    }
}