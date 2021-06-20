package com.javachat.validator;

import com.javachat.model.Board;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class BoardValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return Board.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Board board = (Board) o;

        MyValidateUtil.rejectIfEmptyOrWhitespace(errors, "name", "NotEmpty");
        MyValidateUtil.rejectIfEmptyOrWhitespace(errors, "detail", "NotEmpty");
        
        if (board.getName() == null || board.getName().length() < 3 || board.getName().length() > 100) {
            errors.rejectValue("name", "Size.boardForm.name");
        }
        if (board.getDetail() == null || board.getDetail().length() < 1 || board.getDetail().length() > 700) {
            errors.rejectValue("detail", "Size.boardForm.detail");
        }
        if (!board.isAgreesTerm()) {
            errors.rejectValue("agreesTerm", "NotEmpty.agreesTerm");
        }
    }
}