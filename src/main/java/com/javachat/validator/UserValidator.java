package com.javachat.validator;

import com.javachat.model.User;
import com.javachat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {
    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        User user = (User) o;

        MyValidateUtil.rejectIfEmptyOrWhitespace(errors, "username", "NotEmpty");
        MyValidateUtil.rejectIfEmptyOrWhitespace(errors, "email", "NotEmpty");
        MyValidateUtil.rejectIfEmptyOrWhitespace(errors, "password", "NotEmpty");
        MyValidateUtil.rejectIfEmptyOrWhitespace(errors, "passwordConfirm", "NotEmpty");

        if (user.getUsername() == null || user.getUsername().length() < 3 || user.getUsername().length() > 50) {
            errors.rejectValue("username", "Size.userForm.username");
        }
        if (user.getEmail() == null || user.getEmail().length() < 3 || user.getEmail().length() > 255) {
            errors.rejectValue("email", "Size.userForm.email");
        }
        boolean existsUser = userRepository.existsByName(user.getUsernameNonEmail());
        if (existsUser) {
            errors.rejectValue("username", "Duplicate.userForm.username");
        }
        boolean isValidEmail = MyValidateUtil.isValidEmailAddress(user.getEmail());
        if(!isValidEmail) {
            errors.rejectValue("email", "Format.userForm.email");
        }
        boolean existsEmail = userRepository.existsByEmail(user.getEmail());
        if(existsEmail){
            errors.rejectValue("email", "Format.userForm.notUniqueEmail");
        }
        if (user.getPassword() == null || user.getPassword().length() < 8 || user.getPassword().length() > 50) {
            errors.rejectValue("password", "Size.userForm.password");
        }
        if (!user.getPasswordConfirm().equals(user.getPassword())) {
            errors.rejectValue("passwordConfirm", "Diff.userForm.passwordConfirm");
        }
        if (!user.getAgreesTerm()) {
            errors.rejectValue("agreesTerm", "NotEmpty.agreesTerm");
        }
    }
}