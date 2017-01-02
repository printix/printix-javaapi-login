package net.printix.api.authn;

import net.printix.api.authn.client.LoginManager;
import net.printix.api.authn.client.OAuthToken;
import net.printix.api.authn.client.oauth.Password;
import net.printix.api.authn.client.oauth.TenantName;
import net.printix.api.authn.client.oauth.Username;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static junit.framework.TestCase.assertNotNull;


/**
 * Created by peter on 30-12-16.
 */

public class LoginTest {

    private TestSettings settings;

    public LoginTest() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream("src/test/resources/application.properties"));
        settings = new TestSettings(properties);

    }

    @Test
    public void testLogin() throws IOException {
        OAuthToken oAuthToken = new LoginManager()
                .getOAuthToken(new Username(settings.username),
                        new Password(settings.password),
                new TenantName(settings.tenantName));
        assertNotNull(oAuthToken.getToken());
    }
}
