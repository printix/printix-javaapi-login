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
public class OAuthRefreshCredentials {

	private final String clientId;
	private final String clientSecret;
	private final String refreshToken;


	public OAuthRefreshCredentials(String clientId, String clientSecret, String refreshToken) {
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.refreshToken = refreshToken;
	}


//	public HttpEntity<MultiValueMap<String, String>> asHttpEntitiy() {
//		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
//		map.add("grant_type", "refresh_token");
//		map.add("refresh_token", refreshToken);
//		map.add("client_id", clientId);
//		map.add("client_secret", clientSecret);
//		HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
//		return request;
//	}


	public MultiValueMap<String, String> asFormData() {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("grant_type", "refresh_token");
		map.add("refresh_token", refreshToken);
		map.add("client_id", clientId);
		map.add("client_secret", clientSecret);
		return map;
	}


}
