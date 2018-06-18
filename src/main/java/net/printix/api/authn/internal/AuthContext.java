package net.printix.api.authn.internal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.printix.api.authn.dto.OAuthTokens;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthContext {

	private Object user;
	private OAuthTokens tokens;

}
