package net.printix.api.authn;

import java.util.UUID;

import net.printix.api.authn.dto.OAuthTokens;
import net.printix.api.authn.dto.OAuthTokensForIdCode;
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
	Mono<OAuthTokens> signin(String tenantHostName, UserCredentials userCredentials);

	/**
	 * Signin to printix via ID code.
	 *
	 * @param tenantId      The ID of the tenant to sign into.
	 * @param printerId     The ID of the printer to sign into using the ID code.
	 * @param authContextId The ID of an authentication context on a printer.
	 * @param idCode        The ID code to use to obtain tokens.
	 *
	 * @return Mono providing the tokens.
	 */
	Mono<OAuthTokensForIdCode> signinViaIdCode(UUID tenantId, UUID printerId, String authContextId, String idCode);

	/**
	 * Signin to printix via ID code and pincode.
	 *
	 * @param tenantId      The ID of the tenant to sign into.
	 * @param printerId     The ID of the printer to sign into using the ID code.
	 * @param authContextId The ID of an authentication context on a printer.
	 * @param idCode        The ID code to use to obtain tokens.
	 * @param pincode       The pincode of the ID code.
	 *
	 * @return Mono providing the tokens.
	 */
	Mono<OAuthTokensForIdCode> signinViaIdCode(UUID tenantId, UUID printerId, String authContextId, String idCode, String pincode);

}