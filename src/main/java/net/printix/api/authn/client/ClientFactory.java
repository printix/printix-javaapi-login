package net.printix.api.authn.client;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by peter on 30-12-16.
 */

public class ClientFactory {

    public TokenAuthClient getTokenAuthClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://auth.printix.net")

                .build();
        return retrofit.create(TokenAuthClient.class);
    }


    public LoginClient getLoginClient() {

        return new Retrofit.Builder().baseUrl("https://auth.printix.net")
//                .client(new OkHttpClient.Builder()
//                        .followRedirects(false)
//                        .followSslRedirects(false).build())
                .addConverterFactory(JacksonConverterFactory.create())
                .build().create(LoginClient.class);
    }

}
