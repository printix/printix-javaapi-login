package net.printix.api.authn.internal;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.warrenstrange.googleauth.GoogleAuthenticator;

import net.printix.api.authn.AuthenticationClient;
import net.printix.api.authn.config.OAuthConfig;
import net.printix.api.authn.dto.OAuthTokens;
import net.printix.api.authn.dto.UserCredentials;
import net.printix.api.authn.dto.internal.Jwt;
import net.printix.api.authn.dto.internal.OAuthCredentials;
import net.printix.api.authn.dto.internal.TotpCredentials;
import net.printix.api.authn.dto.internal.UsernamePasswordResponse;
import net.printix.api.authn.exception.InvalidCredentialsException;

/**
 * Authentication client.
 * 
 * @author peter
 */
@Service
public class AuthenticationClientImpl implements AuthenticationClient {

	
	@Autowired
	private OAuthConfig oAuthConfig;
	
	@Value("${printix.domain:printix.net}")
	private String printixDomain;

	
	private final RestTemplate restTemplate;


	/**
	 * The default http client that's loaded with a rest template, follows redirect from
	 * GET requests with no way of intercepting the URI redirected to.
	 * Since not all of these redirects are valid, and some of them contain info we need.
	 * We just disable any redirects
	 */
	private final RestTemplate nonRedirectingRestTemplate;


	public AuthenticationClientImpl() {
		this.restTemplate = new RestTemplate();
		this.nonRedirectingRestTemplate = new RestTemplate(new SimpleClientHttpRequestFactory() {
			@Override
			protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
				super.prepareConnection(connection, httpMethod);
				connection.setInstanceFollowRedirects(false);
			}
		});
	}


	@Override
	public OAuthTokens signin(String tenantHostName, UserCredentials userCredentials) {
		Jwt initialJwt = initiateSignInFlow(tenantHostName).orElseThrow(() -> new RuntimeException("unable to perform initial login"));
		UsernamePasswordResponse usernamePasswordResponse = this.postUsernamePassword(userCredentials, initialJwt);
		if (usernamePasswordResponse.getRequiresMfa()) {
			URI uriFromTotp = postTotpCredentials(usernamePasswordResponse.getJwt(), userCredentials.getTotpSecret());
			String code = getOauthCode(uriFromTotp);
			Optional<OAuthTokens> postOauthCredentials = this.getOauthCredentials(code);
			return postOauthCredentials.orElseThrow(() -> new RuntimeException("unable to signin with mfa"));
		} else {
			String code = getOauthCode(usernamePasswordResponse.getUri());
			Optional<OAuthTokens> postOauthCredentials = this.getOauthCredentials(code);
			return postOauthCredentials.orElseThrow(() -> new RuntimeException("unable to signin as regular user"));
		}
	}


	private Optional<Jwt> initiateSignInFlow(String tenantHostName) {
		URI uri = buildInitialURI(tenantHostName);
		ResponseEntity<String> forEntity = nonRedirectingRestTemplate.getForEntity(uri, String.class);
		HttpHeaders headers = forEntity.getHeaders();
		return Jwt.fromLocation(headers.getLocation());
	}


	/**
	 * Builds the initial uri for contacting the signin service. The only thing this URI is good for, is getting a redirect
	 * that contains an initial Jwt token.
	 */
	private URI buildInitialURI(String tenantHostName) {
		UriComponentsBuilder uri = UriComponentsBuilder.newInstance()
				.scheme("https")
				.host("auth." + printixDomain)
				.path("/oauth/authorize/")
				.queryParam("response_type", "code")
				.queryParam("client_id", oAuthConfig.getClientId())
				.queryParam("client_secret", oAuthConfig.getClientSecret())
				.queryParam("redirect_uri", oAuthConfig.getRedirectUri());
		if (tenantHostName != null && !tenantHostName.isEmpty()) {
			uri.path("tenant").path("/" + tenantHostName + "." + printixDomain);
		}
		return uri.build().toUri();
	}


	/**
	 * posts the supplied username+password to the signin service and returns a response containing a Jwt, 
	 * a redirect and whether or not Mfa authentication is required to get an oauth code 
	 */
	private UsernamePasswordResponse postUsernamePassword(UserCredentials userCredentials, Jwt initialJwt) {
		URI uri = UriComponentsBuilder.newInstance()
				.scheme("https")
				.host("auth." + printixDomain)
				.path("/login")
		        .queryParam("username", userCredentials.getUsername())
		        .queryParam("password", userCredentials.getPassword())
		        .queryParam("jwt", initialJwt.getJwt())
		        .build().toUri();
		RequestEntity<Void> requestEntity = RequestEntity.post(uri)
				.header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.build();

		ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
		URI location = authenticated(responseEntity.getHeaders().getLocation(), "Invalid username/password");
		return new UsernamePasswordResponse(location);
	}


	/**
	 * Builds an oauth token request from the configured properties + the passed in code parameter. 
	 * posts this to the oauth/token service and return the response. Which is hopefully a usable, valid oauth token
	 */
	private Optional<OAuthTokens> getOauthCredentials(String code) {
		OAuthCredentials oAuthCredentials = new OAuthCredentials(oAuthConfig.getGrantType(), code, oAuthConfig.getRedirectUri(), oAuthConfig.getClientId(), oAuthConfig.getClientSecret());
		return Optional.ofNullable(restTemplate.postForObject(oAuthConfig.getSigninUri() + "/oauth/token", oAuthCredentials.asHttpEntitiy(), OAuthTokens.class));
	}


	/**
	 * fetches the URI, reads the Location header from the response, parses a "code" query param from
	 * that, and returns said code as a string
	 */
	private String getOauthCode(URI signinResponse) {
		ResponseEntity<String> redirectWithCode = nonRedirectingRestTemplate.getForEntity(signinResponse, String.class);
		URI location = authenticated(redirectWithCode.getHeaders().getLocation(), "");
		List<NameValuePair> redirectQueryParameters = URLEncodedUtils.parse(location.getQuery(), StandardCharsets.UTF_8);
		NameValuePair code = redirectQueryParameters.stream().filter(nvp -> "code".equals(nvp.getName())).findFirst().orElseThrow(() -> new RuntimeException("no code found in redirect"));
		return code.getValue();
	}


	/**
	 * Generates a valid (by the mfa_seed configured + the current system time) onetime password,
	 *  posts this + the jwt parameter to the authentication service and returns the response redirect
	 */
	private URI postTotpCredentials(Jwt jwtWithMfa, String mfaSeed) {
		GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();
		int totpPassword = googleAuthenticator.getTotpPassword(mfaSeed);
		TotpCredentials totpCredentials = new TotpCredentials(jwtWithMfa.getJwt(), totpPassword);
		URI uri = restTemplate.postForLocation(oAuthConfig.getSigninUri() + "/login", totpCredentials.asHttpEntitiy());
		URI totpResponse = authenticated(uri, "TOTP credentials invalid");
		return totpResponse;
	}


	private URI authenticated(URI uri, String errorMessage){
		if (uri.toString().contains("AUTHN_BAD_CREDENTIALS")) {
			throw new InvalidCredentialsException(errorMessage);
		}
		return uri;
	}


}
