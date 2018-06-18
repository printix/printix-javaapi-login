package net.printix.api.authn;

import net.printix.api.authn.dto.OAuthTokens;
import net.printix.api.authn.dto.UserCredentials;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

public interface TokenManager {


	/**
	 * Sets token to use for given user.
	 * 
	 * OAuthTokens can be obtained using {@link AuthenticationClient#signin(String, net.printix.api.authn.dto.UserCredentials)}.
	 * 
	 * @param user - user id / user name or other key
	 * @param oAuthTokens
	 */
	void setUserToken(Object user, OAuthTokens oAuthTokens);


	/**
	 * Gets token for given user if registered (returns null if not).
	 */
	OAuthTokens getUserTokens(Object user);


	/**
	 * Gets the current oAuth token, refreshing it first if required. 
	 */
	Mono<OAuthTokens> getCurrentTokens();


	/**
	 * Perform given action with given user as current user.
	 * 
	 * OAuthTokens for given user must have been registered using {@linkplain #setUserToken(Object, OAuthTokens)} first.
	 * OAuthTokens can be obtained using {@link AuthenticationClient#signin(String, net.printix.api.authn.dto.UserCredentials)}.
	 *
	 * @param user
	 * @param action
	 */
//	void doAs(Object user, Runnable action);


	/**
	 * Perform given action with given user as current user and returns a result.
	 * 
	 * OAuthTokens for given user must have been registered using {@linkplain #setUserToken(Object, OAuthTokens)} first.
	 * OAuthTokens can be obtained using {@link AuthenticationClient#signin(String, net.printix.api.authn.dto.UserCredentials)}.
	 *
	 * @param user
	 * @param action
	 */
//	<V> V callAs(Object user, Callable<V> action);


	/**
	 * Checks if tokens have been registered for given user.
	 * 
	 * @param user
	 * @return true if tokens for given user has already been registered.
	 */
	boolean hasTokensForUser(Object user);


	/**
	 * Creates a reactor Context with given users tokens.
	 * 
	 * Given tokens must be registered with TokenManager beforehand.
	 * 
	 * @param user
	 * @return Context
	 */
	Context contextFor(Object adminUserName);


	/**
	 * Creates a reactor Context with given users tokens.
	 * 
	 * If given user has already been signed in, and tokens registered with TokenManager under
	 * the userCredentials.username given, the already registered tokens will be used.<br/> 
	 * If not, the user is logged in, and tokens are cached (ie. registered in TokenManager). 
	 * 
	 * @param tenantHostName
	 * @param userCredentials
	 * @return Context
	 * @throws RuntimeException if invalid credentials are given or if login takes too long.
	 */
	Context contextFor(String tenantHostName, UserCredentials userCredentials);


}