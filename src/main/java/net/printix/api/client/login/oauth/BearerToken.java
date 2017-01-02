package net.printix.api.client.login.oauth;

/**
 * Created by peter on 02-01-17.
 */

public class BearerToken {
    private final String token;

    public BearerToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "Bearer "+token;
    }
}
