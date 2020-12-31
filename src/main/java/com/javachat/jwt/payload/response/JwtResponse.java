package com.javachat.jwt.payload.response;

public class JwtResponse {
	private String token;
	private String type = "Bearer";
	private Long id;
	private String usernameNonEmail;
	private String email;
	private String role;

	public JwtResponse(String accessToken, Long id, String usernameNonEmail, String email, String role) {
		this.token = accessToken;
		this.id = id;
		this.usernameNonEmail = usernameNonEmail;
		this.email = email;
		this.role = role;
	}

	public String getAccessToken() {
		return token;
	}

	public void setAccessToken(String accessToken) {
		this.token = accessToken;
	}

	public String getTokenType() {
		return type;
	}

	public void setTokenType(String tokenType) {
		this.type = tokenType;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsernameNonEmail() {
		return usernameNonEmail;
	}

	public void setUsernameNonEmail(String usernameNonEmail) {
		this.usernameNonEmail = usernameNonEmail;
	}

	public String getRole() {
		return role;
	}
}