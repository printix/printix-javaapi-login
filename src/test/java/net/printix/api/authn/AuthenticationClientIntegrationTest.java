package net.printix.api.authn;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpClientErrorException;

import net.printix.api.authn.AuthenticationClientIntegrationTest.TestConfig;
import net.printix.api.authn.dto.OAuthTokens;
import net.printix.api.authn.dto.UserCredentials;
import net.printix.api.authn.exception.InvalidCredentialsException;
import net.printix.api.authn.internal.AuthenticationClientImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TestConfig.class)
public class AuthenticationClientIntegrationTest {

	@Configuration
	@EnableAutoConfiguration
	@EnablePrintixAuthenticationClient
	public static class TestConfig {
	}


	@Autowired
	private AuthenticationClientImpl authClient;


	@Value("${test.auth_client.tenantHostName}")
	private String tenantHostName;

	@Value("${test.auth_client.otherTenantHostName}")
	private String otherTenantHostName;


	@Value("${test.auth_client.adminUserName}")
	private String adminUserName;

	@Value("${test.auth_client.adminPassword}")
	private String adminPassword;

	@Value("${test.auth_client.globalAdminUserName}")
	private String globalAdminUserName;

	@Value("${test.auth_client.globalAdminPassword}")
	private String globalAdminPassword;

	@Value("${test.auth_client.globalAdminTotpSecret}")
	private String globalAdminTotpSecret;


	@Test
	public void testSignin_adminUsingBuiltinAuthentication() {
		OAuthTokens authTokens = authClient.signin(tenantHostName, new UserCredentials(adminUserName, adminPassword)).block();
		assertThat(authTokens).isNotNull();
	}


	@Test
	public void testSignin_adminUsingBuiltinAuthentication_noTenant() {
		OAuthTokens authTokens = authClient.signin(null, new UserCredentials(adminUserName, adminPassword)).block();
		assertThat(authTokens).isNotNull();
	}


	/**
	 * Checks an administrator on one tenant is not granted access to another tenant
	 */
	@Test(expected = InvalidCredentialsException.class)
	public void testSignin_adminUsingBuiltinAuthentication_otherTenant() {
		OAuthTokens authTokens = authClient.signin(otherTenantHostName, new UserCredentials(adminUserName, adminPassword)).block();
		assertThat(authTokens).isNotNull();
	}


	@Test
	public void testSignin_globalAdministrator() {
		OAuthTokens authTokens = authClient.signin(tenantHostName, new UserCredentials(globalAdminUserName, globalAdminPassword, globalAdminTotpSecret)).block();
		assertThat(authTokens).isNotNull();
	}


	@Test
	public void testSignin_globalAdministrator_noTenant() {
		OAuthTokens authTokens = authClient.signin(null, new UserCredentials(globalAdminUserName, globalAdminPassword, globalAdminTotpSecret)).block();
		assertThat(authTokens).isNotNull();
	}


	@Test(expected = InvalidCredentialsException.class)
	public void testSignin_invalidCredentials() {
		authClient.signin(tenantHostName, new UserCredentials("no-such-user", "not_a_password")).block();
	}


	@Test
	public void testSignin_invalidTenant() {
		try {
			String nonExistingTenantHostName = UUID.randomUUID().toString();
			authClient.signin(nonExistingTenantHostName, new UserCredentials("no-such-user", "not_a_password")).block();
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.NOT_FOUND) return; // Expected response. Success!
			fail("Login on non-existing tenant should have returned 404 Not Found- Got http status: " + e.getStatusCode() +
					"\nResponse body received: " + e.getResponseBodyAsString());;
		}
		fail("Signing in on a non-existing tenant seems to have succeeded.");
	}


}
