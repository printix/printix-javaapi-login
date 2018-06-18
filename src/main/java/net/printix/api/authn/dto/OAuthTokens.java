package net.printix.api.authn.dto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

/**
 *
 * @author peter
 */
@Value
public class OAuthTokens {

	@JsonProperty("access_token")
	private final String accessToken;

	@JsonProperty("refresh_token")
	private final String refreshToken;

	@JsonProperty("expires_in")
	private final Long expiresIn;

	@JsonProperty("generated_at")
	private final LocalDateTime generatedAt;

	@JsonCreator
	public OAuthTokens(
			@JsonProperty("access_token") String accessToken,
			@JsonProperty("refresh_token") String refreshToken,
			@JsonProperty("expires_in") Long expiresIn) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.expiresIn = expiresIn;
		this.generatedAt = LocalDateTime.now();
	}


	/**
	 * Checks if the access token is still valid for at least the number of seconds given.
	 * 
	 * @param seconds
	 * @return true if the access token is valid; false if expires within given number of seconds.
	 */
	public boolean isStillValid(long seconds){
		LocalDateTime isExpiredAt = this.generatedAt.plusSeconds(expiresIn);
		return LocalDateTime.now().plus(seconds, ChronoUnit.SECONDS).isBefore(isExpiredAt);
	}


}
