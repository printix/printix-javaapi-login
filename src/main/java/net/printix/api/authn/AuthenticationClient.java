package net.printix.api.authn;

import net.printix.api.authn.dto.OAuthTokens;
import net.printix.api.authn.dto.UserCredentials;
import reactor.core.publisher.Mono;

public interface AuthenticationClient {

	/**
	 * Logs in given user on given tenant.
	 * 
	 * @param tenantHostName 
	 * @param userCredentials
	 * @return a set of oAuth tokens.
	 */
	public Mono<OAuthTokens> signin(String tenantHostName, UserCredentials userCredentials);

}