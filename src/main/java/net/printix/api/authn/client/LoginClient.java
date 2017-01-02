package net.printix.api.authn.client;

import net.printix.api.authn.client.oauth.ClientId;
import net.printix.api.authn.client.oauth.ClientSecret;
import net.printix.api.authn.client.oauth.GrantType;
import net.printix.api.authn.client.oauth.OauthCode;
import net.printix.api.authn.client.oauth.Password;
import net.printix.api.authn.client.oauth.RedirectUri;
import net.printix.api.authn.client.oauth.Username;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by peter on 30-12-16.
 */

public interface LoginClient {

    @POST("/login")
    @FormUrlEncoded
    public Call<ResponseBody> login(@Field("username")Username username, @Field("password")Password password, @Field("jwt")String jwt);

    @POST("/oauth/token")
    @FormUrlEncoded
    public Call<OAuthToken> getOauthToken(@Field("grant_type")GrantType grantType, @Field("code")OauthCode code,
                                          @Field(value = "redirect_uri", encoded = false)RedirectUri redirectUri,
                                          @Field("client_id")ClientId clientId, @Field("client_secret")ClientSecret clientSecret);

}
