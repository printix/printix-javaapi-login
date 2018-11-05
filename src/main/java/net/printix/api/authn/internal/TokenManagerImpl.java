package net.printix.api.authn.internal;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.printix.api.authn.AuthenticationClient;
import net.printix.api.authn.TokenManager;
import net.printix.api.authn.dto.OAuthTokens;
import net.printix.api.authn.dto.UserCredentials;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.context.Context;

@Service
public class TokenManagerImpl implements TokenManager {

	private static final Logger log = LoggerFactory.getLogger(TokenManagerImpl.class);


	@Autowired
	private TokenRefresher tokenRefresher;

	@Autowired
	private AuthenticationClient authenticationClient;

	private ConcurrentHashMap<Object, OAuthTokens> tokensPerUser = new ConcurrentHashMap<>();

	private ThreadLocal<OAuthTokens> tokensForSynchronousCall = new ThreadLocal<>();


	@Override
	public void setUserToken(Object user, OAuthTokens oAuthTokens) {
		if (tokensPerUser.putIfAbsent(user, oAuthTokens) != null) {
			log.warn("Credentials already registered for user {}.", user, new Exception("Stacktrace:"));
		}
		log.trace("Credentials for user {} registered.", user);
	}


	@Override
	public OAuthTokens getTokensFor(Object user) {
		return tokensPerUser.get(user);
	}


	@Override
	public boolean hasTokensForUser(Object user) {
		return tokensPerUser.containsKey(user);
	}


	@Override
	public Mono<OAuthTokens> getCurrentTokens() {
		return Mono.subscriberContext()
				.flatMap(context -> {
					OAuthTokens tokens = context.get(OAuthTokens.class);
					Mono<OAuthTokens> tokensMono = Mono.just(tokens);
					if (tokens.isStillValid(1L)) {
						return tokensMono;
					} else {
						log.trace("Token {} expired or about to expire. Refreshing.", tokens);
						return tokensMono
								.flatMap(t -> tokenRefresher.refresh(Mono.just(t)))
								.flatMap(this::replaceCurrentTokens)
								.map(ctx -> context.get(AuthContext.class).getTokens());
					}
				});
	}


	private Mono<AuthContext> replaceCurrentTokens(OAuthTokens oAuthTokens) {
		return Mono.subscriberContext()
				.map(context -> {
					AuthContext authContext = context.get(AuthContext.class);
					authContext.setTokens(oAuthTokens);
					log.trace("Refreshed tokens for user {}.", authContext.getUser());
					return authContext;
				});
	}


	@Override
	public Context contextFor(Object user) {
		return Context.of(OAuthTokens.class, Optional.ofNullable(getTokensFor(user)).orElseThrow(() -> new RuntimeException("User " + user + " is not registered with AuthManager.")));
	}


	@Override
	public Context contextFor(String tenantHostName, UserCredentials userCredentials) {
		if (hasTokensForUser(userCredentials.getUsername())) {
			return contextFor(userCredentials.getUsername());
		} else {
			return authenticationClient.signin(tenantHostName, userCredentials)
					.map(oAuthTokens -> {
						tokensPerUser.put(userCredentials.getUsername(), oAuthTokens);
						return Context.of(OAuthTokens.class, oAuthTokens);
					}).block(Duration.ofSeconds(30));
		}
	}


	@Override
	public Mono<Void> wrapSyncCall(Runnable syncCall) {
		Mono<Void> result = getCurrentTokens()
				.flatMap(tokens -> {
					Mono<Void> blockingWrapper = Mono.fromRunnable(() -> {
						tokensForSynchronousCall.set(tokens);
						syncCall.run();
						tokensForSynchronousCall.remove();
					});
					blockingWrapper = blockingWrapper.subscribeOn(Schedulers.elastic());
					return blockingWrapper;
				});
		return result;
	}


	@Override
	public <T> Mono<T> wrapSyncCall(Callable<T> syncCall) {
		Mono<T> result = getCurrentTokens()
				.flatMap(tokens -> {
					Mono<T> blockingWrapper = Mono.fromCallable(() -> {
						tokensForSynchronousCall.set(tokens);
						T syncResult = syncCall.call();
						tokensForSynchronousCall.remove();
						return syncResult;
					});
					blockingWrapper = blockingWrapper.subscribeOn(Schedulers.elastic());
					return blockingWrapper;
				});
		return result;
	}


	@Override
	public OAuthTokens getTokensForSynchronousCall() {
		return tokensForSynchronousCall.get();
	}


}
