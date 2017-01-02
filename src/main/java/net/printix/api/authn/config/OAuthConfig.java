package net.printix.api.authn.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * oAuth configuration.
 * 
 * @author peter
 */
@Component
public class OAuthConfig {


	// Names of configuration properties
	private static final String PRINTIX_DOMAIN_PROPERTY = "printix.domain";
	private static final String PRINTIX_OAUTH_CLIENT_ID_PROPERTY = "printix.oauth.client_id";
	private static final String PRINTIX_OAUTH_CLIENT_SECRET_PROPERTY = "printix.oauth.client_secret";
	private static final String PRINTIX_OAUTH_GRANT_TYPE_PROPERTY = "printix.oauth.grant_type";
	private static final String PRINTIX_OAUTH_SIGNIN_URI_SECRET_PROPERTY = "printix.oauth.signin_uri";
	private static final String PRINTIX_OAUTH_REDIRECT_URI_PROPERTY = "printix.oauth.redirect_uri";;


	/**
	 * Printix domain (environment) to use.
	 */
	@Value("${" + PRINTIX_DOMAIN_PROPERTY + ":#{null}}")
	private String domain;

	/**
	 * Id of the calling application.
	 */
	@Value("${" + PRINTIX_OAUTH_CLIENT_ID_PROPERTY + ":#{null}}")
	private String clientId;

	@Value("${" + PRINTIX_OAUTH_CLIENT_SECRET_PROPERTY + ":#{null}}")
	private String clientSecret;

	@Value("${" + PRINTIX_OAUTH_GRANT_TYPE_PROPERTY + ":#{null}}")
	private String grantType;

	@Value("${" + PRINTIX_OAUTH_SIGNIN_URI_SECRET_PROPERTY + ":#{null}}")
	private String signinUri;

	@Value("${" + PRINTIX_OAUTH_REDIRECT_URI_PROPERTY + ":#{null}}")
	private String redirectUri;



	/**
	 * Constructs an empty (invalid) OAuthConfig.
	 * 
	 * Values must be set afterwards, and then verifyConfiguration() should be called to verify the configuration (and fill in defaults for missing  properties).
	 */
	public OAuthConfig() {
	}


	/**
	 * Creates default oAuth configuration with given clientId.
	 * 
	 * The default configuration has the normal setting for accessing the Printix production environment.
	 * 
	 * @param clientId - id of the calling application.
	 */
	public OAuthConfig(String clientId) {
		this.clientId = clientId;
		verifyConfiguration();
	}


	/**
	 * Creates custom configuration.
	 *
	 * @param domain - Printix domain (environment) to access (if null the default 'printix.net' is used).
	 * @param clientId - Id for the calling application.
	 * @param clientSecret - oAuth client secret (if null default is used).
	 * @param grantType - oAuth grant type (if null default is used).
	 * @param signinUri - oAuth signin endpoint (if null default is used).
	 * @param redirectUri - oAuth redirect uri (if null default is used).
	 */
	public OAuthConfig(String domain, String clientId, String clientSecret, String grantType, String signinUri, String redirectUri) {
		this.domain = domain;
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.redirectUri = redirectUri;
		this.signinUri = signinUri;
		this.grantType = grantType;
		verifyConfiguration();
	}


	/**
	 * Checks mandatory configuration properties are present and sets
	 * default values for for other configuration settings not supplied. 
	 */
	@PostConstruct
	public void verifyConfiguration() {
		if (domain == null) domain = "printix.net";
		if (clientId == null) throw new RuntimeException("Missing required configuration property " + PRINTIX_OAUTH_CLIENT_ID_PROPERTY);
		if (clientSecret == null) clientSecret = "thehouseisonfire";
		if (grantType == null) grantType = "authorization_code";
		if (signinUri == null) signinUri = "https://auth." + domain + "/";
		if (redirectUri == null) redirectUri = "https://auth." + domain + "/";
	}


	public String getDomain() {
		return domain;
	}


	public void setDomain(String domain) {
		this.domain = domain;
	}


	public String getClientId() {
		return clientId;
	}


	public void setClientId(String clientId) {
		this.clientId = clientId;
	}


	public String getClientSecret() {
		return clientSecret;
	}


	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}


	public String getGrantType() {
		return grantType;
	}


	public void setGrantType(String grantType) {
		this.grantType = grantType;
	}


	public String getSigninUri() {
		return signinUri;
	}


	public void setSigninUri(String signinUri) {
		this.signinUri = signinUri;
	}


	public String getRedirectUri() {
		return redirectUri;
	}


	public void setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
	}


}
