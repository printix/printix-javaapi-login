package net.printix.api.authn.dto.internal;


import org.apache.http.HttpHeaders;

/**
 *
 * @author peter
 */
public class TotpCredentials {

    private final String jwt;
    private final int totp;
    private final HttpHeaders headers = new HttpHeaders();

    public TotpCredentials(String jwt, int totp) {
        this.jwt = jwt;
        this.totp = totp;
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    }

    public HttpEntity<MultiValueMap<String, String>> asHttpEntitiy() {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("jwt", jwt);
        map.add("oneTimePassword", String.valueOf(totp));
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        return request;
    }

}
