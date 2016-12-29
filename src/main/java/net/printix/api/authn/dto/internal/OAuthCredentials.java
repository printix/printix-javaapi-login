package net.printix.api.authn.dto.internal;


/**
 *
 * @author peter
 */
public class OAuthCredentials {

    private final String grantType;
    private final String code;
    private final String redirectUri;
    private final String clientId;
    private final String clientSecret;

    public OAuthCredentials(String grantType, String code, String redirectUri, String clientId, String clientSecret) {
        this.grantType = grantType;
        this.code = code;
        this.redirectUri = redirectUri;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }


}
