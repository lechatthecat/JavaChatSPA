package com.javachat.validator;

import com.javachat.model.payload.PasswordResetRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class PasswordResetRequestValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return PasswordResetRequest.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        PasswordResetRequest passwordResetRequest = (PasswordResetRequest) o;

        MyValidateUtil.rejectIfEmptyOrWhitespace(errors, "userToken", "NotEmpty");
        MyValidateUtil.rejectIfEmptyOrWhitespace(errors, "password", "NotEmpty");
        MyValidateUtil.rejectIfEmptyOrWhitespace(errors, "passwordConfirm", "NotEmpty");

        if (passwordResetRequest.getUserToken() == null || passwordResetRequest.getUserToken().equals("")) {
            errors.rejectValue("userToken", "UserToken.notEmpty");
        }
        if (passwordResetRequest.getPassword() == null || passwordResetRequest.getPassword().length() < 8 || passwordResetRequest.getPassword().length() > 50) {
            errors.rejectValue("password", "Size.userForm.password");
        }
        if (!passwordResetRequest.getPasswordConfirm().equals(passwordResetRequest.getPassword())) {
            errors.rejectValue("passwordConfirm", "Diff.userForm.passwordConfirm");
        }
    }
}