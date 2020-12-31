package com.javachat.validator;

import com.javachat.model.payload.ProfilePasswordResetRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ProfilePasswordResetRequestValidator implements Validator {
    @Autowired
	AuthenticationManager authenticationManager;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    
    @Override
    public boolean supports(Class<?> aClass) {
        return ProfilePasswordResetRequest.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        ProfilePasswordResetRequest passwordResetRequest = (ProfilePasswordResetRequest) o;

        MyValidateUtil.rejectIfEmptyOrWhitespace(errors, "currentPassword", "NotEmpty");
        MyValidateUtil.rejectIfEmptyOrWhitespace(errors, "password", "NotEmpty");
        MyValidateUtil.rejectIfEmptyOrWhitespace(errors, "passwordConfirm", "NotEmpty");

        if (passwordResetRequest.getCurrentPassword() == null || passwordResetRequest.getCurrentPassword().equals("")) {
            errors.rejectValue("currentPassword", "NotEmpty");
        }
        try {
            authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(passwordResetRequest.getUser().getEmail(), passwordResetRequest.getCurrentPassword()));
        } catch (BadCredentialsException e) {
            errors.rejectValue("currentPassword", "Diff.userForm.currentPassword");
        }
        if (passwordResetRequest.getPassword() == null || passwordResetRequest.getPassword().length() < 8 || passwordResetRequest.getPassword().length() > 50) {
            errors.rejectValue("password", "Size.userForm.password");
        }
        if (!passwordResetRequest.getPasswordConfirm().equals(passwordResetRequest.getPassword())) {
            errors.rejectValue("passwordConfirm", "Diff.userForm.passwordConfirm");
        }
    }
}