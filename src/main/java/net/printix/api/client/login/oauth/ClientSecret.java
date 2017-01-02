package net.printix.api.client.login.oauth;

/**
 * Created by peter on 30-12-16.
 */

public class ClientSecret {
    private final String secret;

    public ClientSecret(String secret) {
        this.secret = secret;
    }

    public String getSecret() {
        return secret;
    }

    @Override
    public String toString() {
        return secret;
    }
}
