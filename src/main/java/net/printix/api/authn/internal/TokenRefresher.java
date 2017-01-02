package net.printix.api.authn.internal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import net.printix.api.authn.config.OAuthConfig;
import net.printix.api.authn.dto.OAuthTokens;
import net.printix.api.authn.dto.internal.OAuthRefreshCredentials;

/**
 * Refreshes OAuthTokens.
 * 
 * @author peter
 * @author Claus Nielsen
 */
@Service
public class TokenRefresher {

	@Autowired
    private OAuthConfig oAuthConfig;

	private RestTemplate restTemplate = new RestTemplate();


	public TokenRefresher() {
	}


	/**
	 * Gets OAuthTokens a fresh access token.
	 * 
	 * @param oAuthTokens
	 * @return copy of given OAuthTokens but with new access token.
	 */
	public OAuthTokens refresh(OAuthTokens oAuthTokens) {
        OAuthRefreshCredentials oAuthRefreshCredentials = new OAuthRefreshCredentials(oAuthConfig.getClientId(), oAuthConfig.getClientSecret(), oAuthTokens.getRefreshToken());
        return restTemplate.postForObject("https://auth." + oAuthConfig.getDomain() + "/oauth/token", oAuthRefreshCredentials.asHttpEntitiy(), OAuthTokens.class);
    }


}
