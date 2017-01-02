package net.printix.api.client.login.oauth;

/**
 * Created by peter on 30-12-16.
 */

public class ClientId {
    private final String clientId;

    public ClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientId() {
        return clientId;
    }

    @Override
    public String toString() {
        return clientId;
    }
}
