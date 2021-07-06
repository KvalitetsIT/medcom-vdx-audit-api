package dk.medcom.audit.client.messaging.nats;

import io.nats.client.Connection;
import io.nats.client.ConnectionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NatsConnectionListener implements ConnectionListener {
    private static final Logger logger = LoggerFactory.getLogger(NatsConnectionListener.class);
    private boolean isReConnecting = false;

    private final NatsConnectionHandler natsConnectionHandler;
    private final Status status;

    public NatsConnectionListener(NatsConnectionHandler natsConnectionHandler) {
        this.natsConnectionHandler = natsConnectionHandler;
        this.status = new Status();
    }

    @Override
    public void connectionEvent(Connection connection, Events events) {
        String logMsg = String.format("A connection event occurred. Connection status: '%s'. Event: '%s'", connection.getStatus(), events.toString());
        if (events == Events.CONNECTED) {
            logger.info(logMsg);
        } else {
            logger.warn(logMsg);
        }

        if (connection.getStatus() == Connection.Status.CONNECTED) {
            status.setIsConnected(1);
        } else {
            status.setIsConnected(0);
        }

        if (!isReConnecting && (events == Events.DISCONNECTED || events == Events.CLOSED)) {
            isReConnecting = true;
            try {
                reConnectNats(connection);
            } finally {
                isReConnecting = false;
            }
        }
    }

    private void reConnectNats(Connection connection) {
        logger.info("Connection lost to NATS. Will try to create now connection.");

        if(!natsConnectionHandler.isShuttingDown()) {
            logger.info("Shutting down. Skipping reconnect to nats.");
            return;
        }

        try {
            connection.close();
        } catch (Exception ignore) {
            // ignore
        }

        while (natsConnectionHandler.getStatus() != Connection.Status.CONNECTED){
            try {
                natsConnectionHandler.connect();
                natsConnectionHandler.reconnected();
            } catch (Exception ex) {
                logger.warn("Trying to connect to NATS, but failed. Retrying...", ex);
            }

            try {
                Thread.sleep(5000); //TODO er der en bedre måde at vente x tid?
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        }
    }
}

class Status {
    //To hold a strong reference
    private double isConnected = 0;

    public double getIsConnected() {
        return isConnected;
    }

    public void setIsConnected(double isConnected) {
        this.isConnected = isConnected;
    }
}
