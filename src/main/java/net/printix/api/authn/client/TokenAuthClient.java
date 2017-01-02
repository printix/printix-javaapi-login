package net.printix.api.authn.client;

import net.printix.api.authn.client.oauth.RedirectUri;
import net.printix.api.authn.client.oauth.TenantName;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by peter on 30-12-16.
 */

public interface TokenAuthClient {


    @GET("/oauth/authorize/tenant/{tenantName}")
    public Call<ResponseBody> initiate(@Path(value="tenantName", encoded = true)TenantName tenantName, @Query("response_type")String responseType, @Query("client_id")String clientId, @Query("client_secret")String clientSecret, @Query(value="redirect_uri", encoded = true)RedirectUri redirectUri);
}
