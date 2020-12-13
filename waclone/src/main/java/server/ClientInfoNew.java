package server;

import java.nio.channels.SocketChannel;

public class ClientInfoNew {
    private String clientId;
    private SocketChannel channel;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public void setChannel(SocketChannel channel) {
        this.channel = channel;
    }

    public ClientInfoNew(String clientId, SocketChannel channel) {
        this.clientId = clientId;
        this.channel = channel;
    }

}
