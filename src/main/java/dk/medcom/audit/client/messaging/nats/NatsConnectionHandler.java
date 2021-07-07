package dk.medcom.audit.client.messaging.nats;

import io.nats.client.Connection;
import io.nats.client.ConnectionListener;
import io.nats.client.ErrorListener;
import io.nats.streaming.Options;
import io.nats.streaming.StreamingConnection;
import io.nats.streaming.StreamingConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class NatsConnectionHandler {
    private static final Logger logger = LoggerFactory.getLogger(NatsConnectionHandler.class);
    private StreamingConnection streamingConnection;
    private final String natsUrl;
    private final String clusterId;
    private final String clientId;

    private final ConnectionListener connectionListener;
    private final ErrorListener errorListener;

    private final List<Consumer<Void>> reconnectListeners = new ArrayList<>();
    private final AtomicBoolean shuttingDown = new AtomicBoolean(false);

    public NatsConnectionHandler(String natsUrl, String clusterId, String clientId) {
        this.natsUrl = natsUrl;
        this.clusterId = clusterId;
        this.clientId = clientId;

        connectionListener = new NatsConnectionListener(this);
        errorListener = new NatsErrorListener();
    }

    public void connect() throws IOException, InterruptedException {
        var options = new Options.Builder()
                .clusterId(clusterId)
                .clientId(clientId)
                .natsUrl(natsUrl)
                .connectionListener(connectionListener)
                .errorListener(errorListener)
                .build();

        StreamingConnectionFactory streamingConnectionFactory = new StreamingConnectionFactory(options);
        streamingConnection = streamingConnectionFactory.createConnection();
    }

    public void close() {
        if (streamingConnection != null) {
            shuttingDown.set(true);
            logger.debug("Closing NATS connection");

            Connection natsConnection = streamingConnection.getNatsConnection();

            if (natsConnection.getStatus() != Connection.Status.CONNECTED) {
                return;
            }

            try {
                logger.info("Flushing NATS connection.");
                natsConnection.flush(Duration.ofSeconds(5));
            } catch (TimeoutException | InterruptedException e) {
                logger.warn("Failed to flush NATS on destroy", e);
            }
            try {
                streamingConnection.close();
                logger.info("NATS connection closed.");
            } catch (InterruptedException | TimeoutException | IOException e) {
                logger.warn("Failed to close NATS connection on destroy", e);
            }
        }
    }

    public boolean isShuttingDown() {
        return shuttingDown.get();
    }

    public StreamingConnection getConnection() {
        return streamingConnection;
    }

    public Connection.Status getStatus() {
        Connection natsConnection = streamingConnection.getNatsConnection();
        if (natsConnection == null) {
            return Connection.Status.CLOSED;
        }
        return natsConnection.getStatus();
    }

    public void addReconnectListener(Consumer<Void> consumer) {
        this.reconnectListeners.add(consumer);
    }

    void reconnected() {
        reconnectListeners.forEach(x -> x.accept(null));
    }

    public String getConnectedUrl() {
        return natsUrl;
    }
}
