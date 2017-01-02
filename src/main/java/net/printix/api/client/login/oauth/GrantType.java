package net.printix.api.client.login.oauth;

/**
 * Created by peter on 30-12-16.
 */

public class GrantType {

    public static  GrantType authorizationCode(){
        return new GrantType("authorization_code");
    }
    private final String grantType;

    public GrantType(String grantType) {
        this.grantType = grantType;
    }

    public String getGrantType() {
        return grantType;
    }

    @Override
    public String toString() {
        return grantType;
    }
}
