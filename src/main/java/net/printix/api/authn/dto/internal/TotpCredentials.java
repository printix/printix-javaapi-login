package net.printix.api.authn.dto.internal;


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
