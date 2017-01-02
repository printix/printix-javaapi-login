package net.printix.api.authn.internal;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.printix.api.authn.TokenManager;
import net.printix.api.authn.dto.OAuthTokens;

@Service
public class TokenManagerImpl implements TokenManager {

	private static final Logger log = LoggerFactory.getLogger(TokenManagerImpl.class);


	@Autowired
	private TokenRefresher tokenRefresher;


	private ConcurrentHashMap<Object, OAuthTokens> tokensPerUser = new ConcurrentHashMap<>();

	private ThreadLocal<Object> currentUser = new ThreadLocal<>();
	private ThreadLocal<OAuthTokens> currentToken = new ThreadLocal<>();

	private OAuthTokens defaultTokens;


	public TokenManagerImpl(TokenRefresher tokenRefresher) {
		this.tokenRefresher = tokenRefresher;
	}


	@Override
	public void setUserToken(Object user, OAuthTokens oAuthTokens) {
		if (tokensPerUser.putIfAbsent(user, oAuthTokens) != null) {
			log.warn("Credentials already registered for user {}.", user);
		}
		log.trace("Credentials for user {} registered.", user);
	}


	@Override
	public void setDefaultToken(OAuthTokens oAuthTokens) {
		this.defaultTokens = oAuthTokens;
	}


	@Override
	public OAuthTokens getCurrentToken() {
		OAuthTokens tokens = currentToken.get();
		if (tokens == null) tokens = defaultTokens;
		if (!tokens.isStillValid(1L)) {
			log.trace("Token {} expired. Refreshing.", tokens);
			tokens = tokenRefresher.refresh(tokens);
			replaceCurrentTokens(tokens);
		}
		return tokens;
	}


	@Override
	public void doAs(Object user, Runnable action) {
		currentUser.set(user);
		currentToken.set(tokensPerUser.get(user));
		try {
			action.run();
		} finally {
			currentUser.remove();
			currentToken.remove();
		}
	}


	private void replaceCurrentTokens(OAuthTokens oAuthTokens) {
		Object user = currentUser.get();
		if (user != null) {
			log.trace("Refreshed tokens for user {}.", user);
			currentToken.set(oAuthTokens);
			tokensPerUser.put(currentUser.get(), oAuthTokens);
		} else {
			defaultTokens = oAuthTokens;
			log.trace("Refreshed default tokens.", user);
		}
	}


}
