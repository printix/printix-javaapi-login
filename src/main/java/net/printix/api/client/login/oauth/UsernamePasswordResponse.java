package net.printix.api.client.login.oauth;

import okhttp3.HttpUrl;

/**
 * Created by peter on 30-12-16.
 */

public class UsernamePasswordResponse {
    private final HttpUrl httpUrl;
    private final JWT jwt;
    private final Boolean requiresMfa;

    public UsernamePasswordResponse(String uri) {
        this.httpUrl = HttpUrl.parse(uri);
        this.jwt = JWT.fromLocation(httpUrl).orElseThrow(() -> new IllegalArgumentException("Invalid Username password response URI, "+ uri.toString()+ " should contain a JWT"));
        this.requiresMfa = uri.toString().contains("oneTimePassword=true");
    }


    public HttpUrl getHttpUrl() {
        return httpUrl;
    }

    public JWT getJwt() {
        return jwt;
    }

    public Boolean getRequiresMfa() {
        return requiresMfa;
    }
}
