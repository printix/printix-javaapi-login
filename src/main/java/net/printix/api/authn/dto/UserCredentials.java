package net.printix.api.authn.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCredentials {

	private String username;
	private String password;
	private String totpSecret;
	
	public UserCredentials(String username, String password) {
		this(username, password, null);
	}

}
