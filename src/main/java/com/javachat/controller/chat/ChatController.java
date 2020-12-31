package com.javachat.controller.chat;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.javachat.model.Board;
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
    
    @MessageMapping("/chat.sendMessage/{board_id}")
    public void sendMessage(
        Principal userPrincipal,
        SimpMessageHeaderAccessor headerAccessor,
        @Payload BoardResponse boardResponse,
        @DestinationVariable String board_id) {

        final List<String> tokenList = headerAccessor.getNativeHeader("Authorization");
        final String token = String.valueOf(tokenList.get(0));
        final String jwt = jwtUtils.parseJwt(token);
		final boolean isValidToken = jwtUtils.validateJwtToken(jwt);
		if (jwt == null || !isValidToken) {
			return;
        }
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(boardResponse, "boardResponse");
        boardResponseValidator.validate(boardResponse, bindingResult);
        if(!bindingResult.hasErrors()){
            UserDetails principal = (UserDetails)((Authentication) userPrincipal).getPrincipal();
            User user = userService.findByEmail(principal.getUsername());
            Board board = boardService.findBoardByTableUrlName(board_id); 
            String ipAddress = (String) headerAccessor.getSessionAttributes().get("ip");
            boardResponse.setIpAddress(ipAddress);
            boardResponse.setUser(user);
            boardResponse.setUserImagePath(user.getUserMainImage().getPath());
            boardResponse.setBoard(board);
            boardResponse.setSender(user.getUsernameNonEmail());
            boardResponse = boardService.createBoardResponse(boardResponse);
            simpMessagingTemplate.convertAndSend("/board/public/"+board_id, boardResponse);
        }
    }

    @MessageMapping("/chat.addUser/{board_id}")
    public void addUser(@Payload BoardResponse boardResponse,
                            SimpMessageHeaderAccessor headerAccessor, @DestinationVariable String board_id) {
        // Authenticate from the token
        final List<String> tokenList = headerAccessor.getNativeHeader("Authorization");
        boardResponse.setMsg_type(BoardResponse.ValidMsgType.JOIN);
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
        simpMessagingTemplate.convertAndSend("/board/public/"+board_id, boardResponse);
    }
}
