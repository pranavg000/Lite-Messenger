package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class ClientInfo {
    private String clientId;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    public ClientInfo(String clientId, DataInputStream inputStream, DataOutputStream outputStream) {
        this.clientId = clientId;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public DataInputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(DataInputStream inputStream) {
        this.inputStream = inputStream;
    }

    public DataOutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(DataOutputStream outputStream) {
        this.outputStream = outputStream;
    }
    
    
}
