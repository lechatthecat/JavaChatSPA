package com.javachat.model.payload;

import javax.validation.constraints.NotBlank;

public class PasswordResetRequest {
    @NotBlank
	private String userToken;

	@NotBlank
    private String password;
    
    @NotBlank
	private String passwordConfirm;

	public String getUserToken() {
		return userToken;
	}

	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
    }
    
    public String getPasswordConfirm() {
		return passwordConfirm;
	}

	public void setConfirmPassword(String passwordConfirm) {
		this.passwordConfirm = passwordConfirm;
	}
}
