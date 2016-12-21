package net.printix.api.authn.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author peter
 */
public class OAuthTokens {

	@JsonProperty("access_token")
    private final String token;

	@JsonProperty("refresh_token")
    private final String refreshToken;

	@JsonProperty("expires_in")
    private final Long expiresIn;
    
	@JsonProperty("generated_at")
    private final LocalDateTime generatedAt;

	@JsonCreator
    public OAuthTokens(@JsonProperty("access_token")String token,@JsonProperty("refresh_token") String refreshToken, @JsonProperty("expires_in")Long expiresIn) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.generatedAt = LocalDateTime.now();
    }

    public String getToken() {
        return token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }
    
    public Boolean isStillValid(){
        LocalDateTime isExpiredAt = this.generatedAt.plusSeconds(expiresIn);
        return LocalDateTime.now().isBefore(isExpiredAt);
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    @Override
    public String toString() {
        return "OAuthToken{" + "token=" + token + ", refreshToken=" + refreshToken + ", expiresIn=" + expiresIn + ", generatedAt=" + generatedAt + '}';
    }
    
}
