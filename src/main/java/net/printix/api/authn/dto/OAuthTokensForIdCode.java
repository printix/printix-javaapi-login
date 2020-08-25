package net.printix.api.authn.dto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

/**
 *
 */
@Value
public class OAuthTokensForIdCode {

	UUID refreshToken;
	UUID accessToken;

	Instant accessTokenExpire;

//	Set<PrintixScope> scopes;

	String accessTokenExpireStr;
	String accessTokenStr;
	String refreshTokenStr;

	@JsonCreator
	public OAuthTokensForIdCode(
			@JsonProperty("refreshToken") UUID refreshToken,
			@JsonProperty("accessToken") UUID accessToken,
			@JsonProperty("accessTokenExpire") Instant accessTokenExpire,
//			@JsonProperty("scopes") Set<String> scopes,
			@JsonProperty("accessTokenExpireStr") String accessTokenExpireStr,
			@JsonProperty("accessTokenStr") String accessTokenStr,
			@JsonProperty("refreshTokenStr") String refreshTokenStr) {
		super();
		this.refreshToken = refreshToken;
		this.accessToken = accessToken;
		this.accessTokenExpire = accessTokenExpire;
//		this.scopes = scopes;
		this.accessTokenExpireStr = accessTokenExpireStr;
		this.accessTokenStr = accessTokenStr;
		this.refreshTokenStr = refreshTokenStr;
	}


}
