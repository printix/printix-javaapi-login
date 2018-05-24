package net.printix.api.authn;

import java.util.concurrent.Callable;

import net.printix.api.authn.dto.OAuthTokens;

public interface TokenManager {


	/**
	 * Sets token to use for given user.
	 * 
	 * OAuthTokens can be obtained using {@link AuthenticationClient#signin(String, net.printix.api.authn.dto.UserCredentials)}.
	 * 
	 * @param user - user id / user name or other key
	 * @param oAuthTokens
	 * @throws RuntimeException if a token is already registered for given user.
	 */
	void setUserToken(Object user, OAuthTokens oAuthTokens);


	/**
	 * Sets a default token.
	 * 
	 * This is useful if using the same credential all the time. It is NOT thread-safe!
	 */
	void setDefaultToken(OAuthTokens oAuthTokens);


	/**
	 * Gets the current oAuth token, refreshing it first if required. 
	 */
	OAuthTokens getCurrentToken();


	/**
	 * Perform given action with given user as current user.
	 * 
	 * OAuthTokens for given user must have been registered using {@linkplain #setUserToken(Object, OAuthTokens)} first.
	 * OAuthTokens can be obtained using {@link AuthenticationClient#signin(String, net.printix.api.authn.dto.UserCredentials)}.
	 *
	 * @param user
	 * @param action
	 */
	void doAs(Object user, Runnable action);


	/**
	 * Perform given action with given user as current user and returns a result.
	 * 
	 * OAuthTokens for given user must have been registered using {@linkplain #setUserToken(Object, OAuthTokens)} first.
	 * OAuthTokens can be obtained using {@link AuthenticationClient#signin(String, net.printix.api.authn.dto.UserCredentials)}.
	 *
	 * @param user
	 * @param action
	 */
	<V> V callAs(Object user, Callable<V> action);


	/**
	 * Checks if tokens have been registered for given user.
	 * 
	 * @param user
	 * @return true if tokens for given user has already been registered.
	 */
	boolean hasTokensForUser(Object user);


}