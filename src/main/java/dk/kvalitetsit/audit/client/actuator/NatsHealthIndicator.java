package dk.kvalitetsit.audit.client.actuator;

import io.nats.client.Connection;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;

public class NatsHealthIndicator extends AbstractHealthIndicator {
    private final Connection natsConnection;

    public NatsHealthIndicator(Connection natsConnection){
        this.natsConnection = natsConnection;
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) {
        Connection.Status status = natsConnection.getStatus();
        if (status == Connection.Status.CONNECTED){
            builder.up()
                    .withDetail("url", natsConnection.getConnectedUrl())
                    .withDetail("status", status.toString());
        }
        else {
            builder.down()
                    .withDetail("url", natsConnection.getConnectedUrl() == null ? "" : natsConnection.getConnectedUrl())
                    .withDetail("status", status.toString());
        }
    }
}