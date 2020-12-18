package server;

import java.nio.channels.SocketChannel;

public class ClientInfo {
    private String clientId;
    private SocketChannel channel;
    private String token;

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
    
    public ClientInfo(String clientId, SocketChannel channel, String token) {
        this.clientId = clientId;
        this.channel = channel;
        this.token = token;
    }

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
