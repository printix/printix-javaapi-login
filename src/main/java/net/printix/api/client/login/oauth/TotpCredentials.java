package net.printix.api.client.login.oauth;


/**
 *
 * @author peter
 */
public class TotpCredentials {

    private final String jwt;
    private final int totp;


    public TotpCredentials(String jwt, int totp) {
        this.jwt = jwt;
        this.totp = totp;
    }



}
