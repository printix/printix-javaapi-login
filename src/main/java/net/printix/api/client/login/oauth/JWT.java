package net.printix.api.client.login.oauth;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.HttpUrl;

/**
 * Created by peter on 30-12-16.
 */

public class JWT {

    private final String jwt;
    private static final Pattern JWT_PARAM = Pattern.compile("jwt=(.+?)(?=(&|$))");

    public JWT(HttpUrl urlFragment) {
        String uriWithoutFragmentDelimiter = urlFragment.toString().replaceAll("#", "");
        HttpUrl parsed = HttpUrl.parse(uriWithoutFragmentDelimiter);
        this.jwt = parsed.queryParameter("jwt");
    }

    private JWT(String jwt){
        this.jwt = jwt;
    }

    public static Optional<JWT> fromLocation(HttpUrl url){
        Matcher matcher = JWT_PARAM.matcher(url.toString());
        if(matcher.find() && matcher.groupCount() == 2){
            String jwt = matcher.group(1);
            return Optional.ofNullable(new JWT(jwt));
        }else{
            return Optional.empty();
        }
    }

    public String getJwt(){
        return jwt;
    }
}
