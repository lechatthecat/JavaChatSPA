package com.javachat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class WebSocketAuthorizationSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {
    @Override
    protected void configureInbound(final MessageSecurityMetadataSourceRegistry messages) {
        // Websokect authorization mapping.
        messages
        .simpTypeMatchers(
            SimpMessageType.CONNECT,
            SimpMessageType.DISCONNECT,
            SimpMessageType.OTHER
        ).permitAll()
        .anyMessage().authenticated();
    }
}
