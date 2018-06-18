package net.printix.api.authn.dto.internal;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

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

//    public HttpEntity<MultiValueMap<String, String>> asHttpEntitiy() {
//        MultiValueMap<String, String> map = asFormData();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
//        return request;
//    }

	public MultiValueMap<String, String> asFormData() {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", grantType);
        map.add("code", code);
        map.add("redirect_uri", redirectUri);
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
		return map;
	}

}
