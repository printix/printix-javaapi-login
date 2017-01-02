package net.printix.api.client.login.oauth;

import net.printix.api.client.ClientFactory;
import net.printix.api.dto.OAuthToken;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by peter on 31-12-16.
 */

public class LoginManager {

    public OAuthToken getOAuthToken(Username username, Password
            password, TenantName tenantName) throws IOException {
        RedirectUri https = new RedirectUri(new HttpUrl.Builder().host(tenantName.getTenantName()).scheme("https").toString());
//        RedirectUri redirectUri = new RedirectUri("https://apitest.printix.net");
        ClientFactory clientFactory = new ClientFactory();

        TokenAuthClient tokenAuthClient = clientFactory.getTokenAuthClient();

        Response<ResponseBody> execute = tokenAuthClient.initiate(tenantName,"code", "web_app", "thehouseisonfire", https).execute();

        JWT jwt = new JWT(execute.raw().request().url());

        Response<ResponseBody> execute1 = clientFactory.getLoginClient().login(username, password, jwt.getJwt()).execute();

        HttpUrl firstRedirect =execute1.raw().request().url();

        okhttp3.Response execute2 = new OkHttpClient.Builder().followRedirects(false).followSslRedirects(false)
                .build().newCall(new Request.Builder().get().url(firstRedirect).build()).execute();

        String code =
                execute2.request().url()
                        .queryParameter("code");
        return clientFactory.getLoginClient()
                .getOauthToken(GrantType.authorizationCode(), new OauthCode(code),
                        https, new ClientId("web_app"), new ClientSecret("thehouseisonfire")).execute().body();

    }
}
