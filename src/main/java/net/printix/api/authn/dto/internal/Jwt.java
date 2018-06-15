package net.printix.api.authn.dto.internal;

import java.net.URI;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author peter
 */
public class Jwt {
    
    private final String jwt;
    private static final Pattern JWT_PARAM = Pattern.compile("jwt=(.+?)(?=(&|$))");

    private Jwt(String jwt) {
        this.jwt = jwt;
    }

    public String getJwt() {
        return jwt;
    }
    
    public static Optional<Jwt> fromLocation(URI location) {
    	if (location == null) return Optional.empty();
        Matcher matcher = JWT_PARAM.matcher(location.toString());
        if (matcher.find() && matcher.groupCount() == 2) {
            String jwt = matcher.group(1);
            return Optional.ofNullable(new Jwt(jwt));
        } else {
            return Optional.empty();
        }
    }
}
