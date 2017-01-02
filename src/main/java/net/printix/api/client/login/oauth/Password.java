package net.printix.api.client.login.oauth;

/**
 * Created by peter on 31-12-16.
 */

public class Password {

    private final String password;

    public Password(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return password;
    }
}
