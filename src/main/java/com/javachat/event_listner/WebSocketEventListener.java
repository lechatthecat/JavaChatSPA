package com.javachat.event_listner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.javachat.model.BoardResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
		logger.info(headerAccessor.toString());
        logger.info("Received a new web socket connection");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        logger.info(headerAccessor.toString());
        String username = String.valueOf(headerAccessor.getSessionAttributes().get("username"));
        String string_board_id = String.valueOf(headerAccessor.getSessionAttributes().get("board_id"));
        if(username != null && string_board_id != null 
            && !username.equals("") && !string_board_id.equals("")) {

            logger.info("User Disconnected : " + username);

            BoardResponse boardResponse = new BoardResponse();
            //boardResponse.setResponse(username + " left!");
            boardResponse.setMsg_type(BoardResponse.ValidMsgType.LEAVE);
            boardResponse.setSender(username);

            messagingTemplate.convertAndSend("/board/public/"+string_board_id, boardResponse);
        }
    }
}
