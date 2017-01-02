package net.printix.api.authn.client.oauth;

/**
 * Created by peter on 31-12-16.
 */

public class Username {
    private final String username;

    public Username(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return username;
    }
}
