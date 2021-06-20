package com.javachat.validator;

import com.javachat.model.BoardResponse;
import com.javachat.service.BoardService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class BoardResponseValidator implements Validator {
    @Autowired
    private BoardService boardResponseService;
    private String boardId;

    public void setBoardId(String boardId) {
        this.boardId = boardId;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return BoardResponse.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        BoardResponse boardResponse = (BoardResponse) o;
        MyValidateUtil.rejectIfEmptyOrWhitespace(errors, "response", "NotEmpty");
        MyValidateUtil.rejectIfEmptyOrWhitespace(errors, "sender", "NotEmpty");
        
        if (boardResponse.getSender() == null || boardResponse.getSender().length() < 3 || boardResponse.getSender().length() > 50) {
            errors.rejectValue("sender", "Size.userForm.username");
        }
        if (boardResponse.getResponse() == null || boardResponse.getResponse().length() < 1 || boardResponse.getResponse().length() > 700) {
            errors.rejectValue("response", "Size.boardResponseForm.response");
        }
        int lastResNumber = boardResponseService.findLastBRIdByTableUrlName(this.boardId);
        if (lastResNumber >= 1000) {
            errors.rejectValue("response", "Size.board.tooManyResponse");
        }
    }
}
