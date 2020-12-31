package com.javachat.model.payload;

import javax.validation.constraints.NotBlank;

import com.javachat.model.User;

public class ProfilePasswordResetRequest {
    @NotBlank
	private String currentPassword;

	@NotBlank
    private String password;
    
    @NotBlank
	private String passwordConfirm;

	private User user;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getCurrentPassword() {
		return currentPassword;
	}

	public void setCurrentPassword(String currentPassword) {
		this.currentPassword = currentPassword;
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
