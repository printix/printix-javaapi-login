package net.printix.api.authn.internal;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse.Headers;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.warrenstrange.googleauth.GoogleAuthenticator;

import lombok.extern.slf4j.Slf4j;
import net.printix.api.authn.AuthenticationClient;
import net.printix.api.authn.config.OAuthConfig;
import net.printix.api.authn.dto.OAuthTokens;
import net.printix.api.authn.dto.UserCredentials;
import net.printix.api.authn.dto.internal.Jwt;
import net.printix.api.authn.dto.internal.OAuthCredentials;
import net.printix.api.authn.dto.internal.TotpCredentials;
import net.printix.api.authn.dto.internal.UsernamePasswordResponse;
import net.printix.api.authn.exception.InvalidCredentialsException;
import reactor.core.publisher.Mono;

/**
 * Authentication client.
 * 
 * @author peter
 */
@Service
@Slf4j
public class AuthenticationClientImpl implements AuthenticationClient {


	@Autowired
	private OAuthConfig oAuthConfig;

	@Value("${printix.domain:printix.net}")
	private String printixDomain;


	private final WebClient webClient;
	private final WebClient nonRedirectingWebClient; // "non-redirecting" because we need to intercept redirects.

	private final Encoder base64Encoder = Base64.getEncoder();


	public AuthenticationClientImpl(WebClient.Builder webClientBuilder) {
		// TODO Spring 5.1 should support per-webClient configuration of whether redirects should be followed or not. Until then both clients do NOT follow redirects.
		// It has has to be specified per request on the HttpClientRequest
		// https://stackoverflow.com/questions/47655789/how-to-make-reactive-webclient-follow-3xx-redirects?rq=1
		// https://github.com/reactor/reactor-netty/issues/235
		 this.webClient = webClientBuilder
				 .filter(ExchangeFilterFunctions.statusError(HttpStatus::is4xxClientError, cr -> new HttpClientErrorException(cr.statusCode())))
				 .filter(ExchangeFilterFunctions.statusError(HttpStatus::is5xxServerError, cr -> new HttpClientErrorException(cr.statusCode())))
				 .build();
		 this.nonRedirectingWebClient = webClient; // Builder.build();
	}


	@Override
	public Mono<OAuthTokens> signin(String tenantHostName, UserCredentials userCredentials) {
		log.trace("Initiating login flow.");
		return initiateSignInFlow(tenantHostName)
				.map(optionalJwt -> optionalJwt.orElseThrow(() -> new RuntimeException("Unable to signin.")))
				.flatMap(initialJwt -> postUsernamePassword(userCredentials, initialJwt))
				.flatMap(usernamePasswordResponse -> {
					if (usernamePasswordResponse.getRequiresMfa()) {
						Mono<URI> uriFromTotp = postTotpCredentials(usernamePasswordResponse.getJwt(), userCredentials.getTotpSecret());
						return uriFromTotp.flatMap(this::getOauthCode);
					} else {
						return getOauthCode(usernamePasswordResponse.getUri());
					}
				})
				.flatMap(this::getOauthCredentials);
	}


	@Override
	public Mono<OAuthTokens> signinViaIdCode(UUID tenantId, UUID printerId, String idCode) {
		return signinViaIdCode(tenantId, printerId, idCode, null);
	}


	@Override
	public Mono<OAuthTokens> signinViaIdCode(UUID tenantId, UUID printerId, String idCode, String pincode) {
		MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
		request.add("grant_type", "password");
		request.add("client_id", oAuthConfig.getClientId());
		request.add("client_secret", oAuthConfig.getClientSecret());
		request.add("secret", base64Encoder.encodeToString(idCode.getBytes()));
		if (pincode != null && !pincode.isEmpty()) {
			request.add("pincode", pincode);
		}

		return nonRedirectingWebClient.post()
				.uri("https://auth." + printixDomain + "/oauth/token/tenants/{tenant}/printers/{printer}/usersecret",
						tenantId, printerId)
				.contentType(APPLICATION_FORM_URLENCODED)
				.body(BodyInserters.fromFormData(request))
				.accept(APPLICATION_JSON)
				.exchange()
				.flatMap(cr -> cr.bodyToMono(OAuthTokens.class));
	}


	private Mono<Optional<Jwt>> initiateSignInFlow(String tenantHostName) {
		return nonRedirectingWebClient.get()
				.uri(buildInitialURI(tenantHostName))
				.exchange()
				.map(cr -> {
					cr.bodyToMono(Void.class); // There is no content. This is to release resources. 
					Headers headers = cr.headers();
					Optional<Jwt> optJwt = Jwt.fromLocation(headers.asHttpHeaders().getLocation()); //.orElseThrow(() -> new RuntimeException("Unable to perform initial login - missing JWT."));
					return optJwt;
				});
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
	private Mono<UsernamePasswordResponse> postUsernamePassword(UserCredentials userCredentials, Jwt initialJwt) {
		log.trace("Posting username and password.");
		URI uri;
		try {
			uri = UriComponentsBuilder.newInstance()
					.scheme("https")
					.host("auth." + printixDomain)
					.path("/login")
					.queryParam("username", URLEncoder.encode(userCredentials.getUsername(), "utf-8"))
					.queryParam("password", URLEncoder.encode(userCredentials.getPassword(), "utf-8"))
					.queryParam("jwt", initialJwt.getJwt())
					.build(true).toUri();
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return webClient.post().uri(uri)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.exchange()
				.map(cr -> {
					cr.bodyToMono(Void.class);  // There is no content. This is to release resources.
					URI location = authenticated(cr.headers().asHttpHeaders().getLocation(), "Invalid username/password");
					return new UsernamePasswordResponse(location);
				});
	}


	/**
	 * Builds an oauth token request from the configured properties + the passed in code parameter. 
	 * posts this to the oauth/token service and return the response. Which is hopefully a usable, valid oauth token
	 */
	private Mono<OAuthTokens> getOauthCredentials(String code) {
		log.trace("getOauthCredentials.");
		OAuthCredentials oAuthCredentials = new OAuthCredentials(oAuthConfig.getGrantType(), code, oAuthConfig.getRedirectUri(), oAuthConfig.getClientId(), oAuthConfig.getClientSecret());
		return webClient.post()
				.uri(asUri(oAuthConfig.getSigninUri() + "/oauth/token"))
				.body(BodyInserters.fromFormData(oAuthCredentials.asFormData()))
				.retrieve()
				.bodyToMono(OAuthTokens.class);
	}


	/**
	 * fetches the URI, reads the Location header from the response, parses a "code" query param from
	 * that, and returns said code as a string
	 */
	private Mono<String> getOauthCode(URI signinResponse) {
		log.trace("getOauthCode.");
		return nonRedirectingWebClient.get()
				.uri(signinResponse)
				.exchange()
				.map(cr -> {
					cr.bodyToMono(Void.class);  // There is no content. This is to release resources.
					URI location = authenticated(cr.headers().asHttpHeaders().getLocation(), "");
					List<NameValuePair> redirectQueryParameters = URLEncodedUtils.parse(location.getQuery(), StandardCharsets.UTF_8);
					NameValuePair code = redirectQueryParameters.stream().filter(nvp -> "code".equals(nvp.getName())).findFirst().orElseThrow(() -> new RuntimeException("no code found in redirect"));
					return code.getValue();
				});
	}


	/**
	 * Generates a valid (by the mfa_seed configured + the current system time) onetime password,
	 *  posts this + the jwt parameter to the authentication service and returns the response redirect
	 */
	private Mono<URI> postTotpCredentials(Jwt jwtWithMfa, String mfaSeed) {
		log.trace("postTotpCredentials.");
		GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();
		int totpPassword = googleAuthenticator.getTotpPassword(mfaSeed);
		TotpCredentials totpCredentials = new TotpCredentials(jwtWithMfa.getJwt(), totpPassword);
		return webClient.post()
				.uri(asUri(oAuthConfig.getSigninUri() + "/login"))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.body(BodyInserters.fromFormData(totpCredentials.asFormData()))
				.exchange()
				.map(cr -> {
					cr.bodyToMono(Void.class);  // There is no content. This is to release resources.
					return authenticated(cr.headers().asHttpHeaders().getLocation(), "TOTP credentials invalid.");
				});
	}


	private URI asUri(String uri) {
		try {
			return new URI(uri);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}


	private URI authenticated(URI uri, String errorMessage){
		if (uri.toString().contains("AUTHN_BAD_CREDENTIALS")) {
			throw new InvalidCredentialsException(errorMessage);
		}
		return uri;
	}


}
