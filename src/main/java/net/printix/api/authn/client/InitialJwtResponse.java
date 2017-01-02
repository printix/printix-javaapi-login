package net.printix.api.authn.client;

import okhttp3.HttpUrl;

/**
 * Created by peter on 30-12-16.
 */

public class InitialJwtResponse {

    private final String fragment;

    public InitialJwtResponse(HttpUrl urlFragment) {
        String uriWithoutFragmentDelimiter = urlFragment.toString().replaceAll("#", "");
        HttpUrl parsed = HttpUrl.parse(uriWithoutFragmentDelimiter);
        this.fragment = parsed.queryParameter("jwt");
    }

    public String getJwt(){
        return fragment;
    }
}
