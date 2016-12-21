package net.printix.api.authn.dto.internal;

import java.net.URI;

/**
 *
 * @author peter
 */
public class UsernamePasswordResponse {

	private final URI uri;
    private final Jwt jwt;
    private final Boolean requiresMfa;

    public UsernamePasswordResponse(URI uri) {
        this.uri = uri;
        this.jwt = Jwt.fromLocation(uri).orElseThrow(() -> new IllegalArgumentException("Invalid Username password response URI, "+ uri.toString()+ " should contain a JWT"));
        this.requiresMfa = uri.toString().contains("oneTimePassword=true");
    }
    

    public URI getUri() {
        return uri;
    }

    public Jwt getJwt() {
        return jwt;
    }

    public Boolean getRequiresMfa() {
        return requiresMfa;
    }
    
    
}
