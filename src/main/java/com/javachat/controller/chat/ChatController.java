package com.javachat.controller.chat;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import java.security.Principal;
import java.util.List;

import com.javachat.service.BoardService;
import com.javachat.util.JwtUtils;
import com.javachat.validator.BoardResponseValidator;
import com.javachat.service.UserDetailsServiceImpl;
import com.javachat.service.UserService;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import com.javachat.model.BoardResponse;
import com.javachat.model.User;

@Controller
public class ChatController {
    @Autowired
    UserService userService;
    @Autowired
	BoardService boardService;
    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;
	@Autowired
	JwtUtils jwtUtils;
    @Autowired
    UserDetailsServiceImpl userDetailsService;
    @Autowired
    BoardResponseValidator boardResponseValidator;
    @Autowired
    MessageSource messageSource;
    
    @MessageMapping("/chat.sendMessage/{boardId}")
    public void sendMessage(
        Principal userPrincipal,
        SimpMessageHeaderAccessor headerAccessor,
        @Payload BoardResponse boardResponse,
        @DestinationVariable String boardId) {

        final List<String> tokenList = headerAccessor.getNativeHeader("Authorization");
        final String token = String.valueOf(tokenList.get(0));
        final String jwt = jwtUtils.parseJwt(token);
		final boolean isValidToken = jwtUtils.validateJwtToken(jwt);
        User user = null;
        if (jwt == null || !isValidToken) {
			user = userService.findByEmail("Anonymous guest");
            boardResponse.setSender(user.getUsernameNonEmail());
        } else {
            UserDetails principal = (UserDetails)((Authentication) userPrincipal).getPrincipal();
            user = userService.findByEmail(principal.getUsername());
        }
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(boardResponse, "boardResponse");
        boardResponseValidator.setBoardId(boardId);
        boardResponseValidator.validate(boardResponse, bindingResult);
        if(!bindingResult.hasErrors()){
            String ipAddress = (String) headerAccessor.getSessionAttributes().get("ip");
            boardResponse.setMsgType(BoardResponse.ValidMsgType.CHAT);
            boardResponse = boardService.createBoardResponse(user, boardId, ipAddress, boardResponse);
            try {
                simpMessagingTemplate.convertAndSend("/board/public/"+boardId, boardResponse);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        } else {
            final List<FieldError> errors = bindingResult.getFieldErrors();
            boardResponse.setResponse("");
            for (FieldError error : errors ) {
                final String message = messageSource.getMessage(error, null);
                boardResponse.setResponse(boardResponse.getResponse() + "\n\r" + message);
            }
            boardResponse.setSender("Admin");
            boardResponse.setMsgType(BoardResponse.ValidMsgType.ERROR);
            simpMessagingTemplate.convertAndSend("/board/public/"+boardId, boardResponse);
        }
    }

    @MessageMapping("/chat.addUser/{board_id}")
    public void addUser(@Payload BoardResponse boardResponse,
                            SimpMessageHeaderAccessor headerAccessor, @DestinationVariable String board_id) {
        // Authenticate from the token
        final List<String> tokenList = headerAccessor.getNativeHeader("Authorization");
        boardResponse.setMsgType(BoardResponse.ValidMsgType.JOIN);
        if (tokenList != null) {
            String token = String.valueOf(tokenList.get(0));
            token = jwtUtils.parseJwt(token);
            final boolean isValidToken = jwtUtils.validateJwtToken(token);
            // If the token is valid, set the user
            if (isValidToken) {
                final String email = jwtUtils.getUsernameFromToken(token);
                final User user = (User) userDetailsService.loadUserByUsername(email);
                headerAccessor.getSessionAttributes().put("username", user.getUsernameNonEmail());
                boardResponse.setSender(user.getUsernameNonEmail());
            }
        } else {
            headerAccessor.getSessionAttributes().put("username", "guest");
            boardResponse.setSender("guest");
        }
        headerAccessor.getSessionAttributes().put("board_id", board_id);
        try {
            simpMessagingTemplate.convertAndSend("/board/public/"+board_id, boardResponse);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
