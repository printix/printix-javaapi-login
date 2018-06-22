package net.printix.api.authn.dto.internal;

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


	public MultiValueMap<String, String> asFormData() {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("grant_type", "refresh_token");
		map.add("refresh_token", refreshToken);
		map.add("client_id", clientId);
		map.add("client_secret", clientSecret);
		return map;
	}


}
