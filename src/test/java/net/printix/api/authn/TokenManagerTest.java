package net.printix.api.authn;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.groups.Tuple;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import lombok.extern.slf4j.Slf4j;
import net.printix.api.authn.AuthenticationClientIntegrationTest.TestConfig;
import net.printix.api.authn.dto.OAuthTokens;
import net.printix.api.authn.dto.UserCredentials;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TestConfig.class)
@Slf4j
public class TokenManagerTest {

	private static final int RANGE = 20;
	private static final int[] RANGE_VALUES = IntStream.range(0,  RANGE).toArray();

	@Configuration
	@EnableAutoConfiguration
	@EnablePrintixAuthenticationClient
	public static class TestConfig {
	}


	@Value("${test.auth_client.tenantHostName}")
	private String tenantHostName;

	@Value("${test.auth_client.adminUserName}")
	private String adminUserName;

	@Value("${test.auth_client.adminPassword}")
	private String adminPassword;

	@Value("${test.auth_client.globalAdminUserName}")
	private String globalUserName;

	@Value("${test.auth_client.globalAdminPassword}")
	private String globalPassword;

	@Value("${test.auth_client.globalAdminTotpSecret}")
	private String globalAdminTotpSecret;

	@Autowired
	private TokenManager tokenManager;


	@Test
	public void testTokenManager() {
		List<Flux<Tuple>> fluxes = Stream.of(new UserCredentials(adminUserName, adminPassword), new UserCredentials(globalUserName, globalPassword, globalAdminTotpSecret)).map(credentials -> {
			return Flux.range(0, RANGE).delayElements(Duration.ofMillis(1))
					.flatMap(i -> tokenManager.getCurrentTokens().map(currentTokens -> {
						String user = credentials.getUsername();
						OAuthTokens userTokens = tokenManager.getUserTokens(user);
						assertThat(currentTokens).as("Current tokens are not the right tokens for " + user).isEqualTo(userTokens);
						return new Tuple(user, i, Instant.now());
					}))
					.subscriberContext(tokenManager.contextFor(tenantHostName, credentials))
					.publishOn(Schedulers.elastic());
		}).collect(toList());

		
		StepVerifier.create(Flux.merge(fluxes).publishOn(Schedulers.elastic()))
		.recordWith(ArrayList::new)
		.expectNextCount(RANGE * 2)
		.consumeRecordedWith(r -> {
			Stream.of(adminUserName, globalUserName).forEach(userName -> {
				int[] numbersEmmitetForUser = r.stream()
						.filter(t -> t.toList().get(0).equals(userName))
						.map(t -> (Integer)t.toList().get(1))
						.mapToInt(Integer::intValue)
						.toArray();
				assertThat(numbersEmmitetForUser).as("0.." + (RANGE - 1) + " should have been emmited in-order for user " + userName + ".").containsExactly(RANGE_VALUES);
			});
			Map<String, Instant> userFirstEmission = new HashMap<>();
			Map<String, Instant> userLastEmission = new HashMap<>();
			// Verify the two fluxes weren't processes sequentially, one after another.
			r.stream().forEach(t -> {
				List<?> values = t.toList();
				String user = (String) values.get(0);
				Integer n = (Integer) values.get(1);
				Instant emitted = (Instant) values.get(2);
				userFirstEmission.putIfAbsent(user, emitted);
				userLastEmission.put(user, emitted);
				log.info(user + ":\t" + n + " at " + emitted);
			});
			SoftAssertions bundle = new SoftAssertions();
			bundle.assertThat(userFirstEmission.get(adminUserName))
			.as("Fluxes should have been merged, so 1st emission for admin user should be befor last emission for global user.")
			.isBefore(userLastEmission.get(globalUserName));
			bundle.assertThat(userFirstEmission.get(globalUserName))
			.as("Fluxes should have been merged, so 1st emission for global user should be befor last emission for admin user.")
			.isBefore(userLastEmission.get(adminUserName));
			bundle.assertAll();
		})
		.verifyComplete();
	}


}
