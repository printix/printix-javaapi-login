package net.printix.api.authn;

import net.printix.api.client.ClientFactory;
import net.printix.api.client.login.oauth.LoginManager;
import net.printix.api.client.login.oauth.Password;
import net.printix.api.client.login.oauth.TenantName;
import net.printix.api.client.login.oauth.Username;
import net.printix.api.dto.OAuthToken;
import net.printix.api.dto.PrinterList;
import net.printix.api.dto.TenantId;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;

import retrofit2.Response;

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

    @Test
    public void testGetPrinters() throws IOException {
        OAuthToken oAuthToken = new LoginManager()
                .getOAuthToken(new Username(settings.username),
                        new Password(settings.password),
                        new TenantName(settings.tenantName));
        Response<PrinterList> printers = new ClientFactory().getApiClient(oAuthToken).getPrinters(new TenantId(UUID.fromString("cfe2385d-18f6-44da-85f9-3915f6fe1048")));
        System.out.println("whuuut");
    }
}
