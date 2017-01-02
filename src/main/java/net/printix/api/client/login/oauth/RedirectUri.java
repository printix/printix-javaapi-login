package net.printix.api.client.login.oauth;

/**
 * Created by peter on 30-12-16.
 */

public class RedirectUri {

    private final String uri;

    public RedirectUri(String uri) {

        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    @Override
    public String toString() {
        return uri;
    }
}
