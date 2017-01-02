package net.printix.api.authn.client;

import net.printix.api.authn.dto.internal.Jwt;

import okhttp3.HttpUrl;

/**
 * Created by peter on 30-12-16.
 */

public class UsernamePasswordResponse {
    private final HttpUrl httpUrl;
    private final Jwt jwt;
    private final Boolean requiresMfa;

    public UsernamePasswordResponse(String uri) {
        this.httpUrl = HttpUrl.parse(uri);
        this.jwt = Jwt.fromLocation(httpUrl.uri()).orElseThrow(() -> new IllegalArgumentException("Invalid Username password response URI, "+ uri.toString()+ " should contain a JWT"));
        this.requiresMfa = uri.toString().contains("oneTimePassword=true");
    }


    public HttpUrl getHttpUrl() {
        return httpUrl;
    }

    public Jwt getJwt() {
        return jwt;
    }

    public Boolean getRequiresMfa() {
        return requiresMfa;
    }
}
