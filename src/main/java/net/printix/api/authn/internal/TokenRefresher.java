package net.printix.api.authn.internal;

import static java.util.Collections.emptyMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;

import net.printix.api.authn.config.OAuthConfig;
import net.printix.api.authn.dto.OAuthTokens;
import net.printix.api.authn.dto.internal.OAuthRefreshCredentials;
import reactor.core.publisher.Mono;

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

	private WebClient webClient;


	public TokenRefresher(WebClient.Builder webClientBuilder) {
		this.webClient = webClientBuilder
				.filter(ExchangeFilterFunctions.statusError(HttpStatus::is4xxClientError, cr -> new HttpClientErrorException(cr.statusCode())))
				.filter(ExchangeFilterFunctions.statusError(HttpStatus::is5xxServerError, cr -> new HttpClientErrorException(cr.statusCode())))
				.build();
	}


	/**
	 * Gets OAuthTokens a fresh access token.
	 * 
	 * @param oAuthTokens
	 * @return copy of given OAuthTokens but with new access token.
	 */
	public Mono<OAuthTokens> refresh(Mono<OAuthTokens> oAuthTokensMono) {
		return oAuthTokensMono.flatMap(oAuthTokens -> {
			OAuthRefreshCredentials oAuthRefreshCredentials = new OAuthRefreshCredentials(oAuthConfig.getClientId(), oAuthConfig.getClientSecret(), oAuthTokens.getRefreshToken());
			return webClient.post()
					.uri("https://auth." + oAuthConfig.getDomain() + "/oauth/token", emptyMap())
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.body(BodyInserters.fromFormData(oAuthRefreshCredentials.asFormData()))
					.retrieve()
					.bodyToMono(OAuthTokens.class);
		});
	}


}
