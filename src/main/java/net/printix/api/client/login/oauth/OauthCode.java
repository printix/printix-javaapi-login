package net.printix.api.client.login.oauth;

/**
 * Created by peter on 30-12-16.
 */

public class OauthCode {

    private final String code;

    public OauthCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return code;
    }
}
