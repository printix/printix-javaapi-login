package net.printix.api.authn.dto;

public class UserCredentials {

	private String username;
	private String password;
	private String totpSecret;
	
	public UserCredentials() {
	}
	
	public UserCredentials(String username, String password, String totpSecret) {
		this.username = username;
		this.password = password;
		this.totpSecret = totpSecret;
	}

	public UserCredentials(String username, String password) {
		this(username, password, null);
	}

	public String getUsername() {
		return username;
	}
	
	
	public void setUsername(String userName) {
		this.username = userName;
	}
	
	
	public String getPassword() {
		return password;
	}
	
	
	public void setPassword(String password) {
		this.password = password;
	}

	
	public String getTotpSecret() {
		return totpSecret;
	}
	
	
	public void setTotpSecret(String mfaToken) {
		this.totpSecret = mfaToken;
	}
	

}
