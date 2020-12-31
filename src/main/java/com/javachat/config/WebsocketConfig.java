package com.javachat.config;

import java.util.List;
import java.util.UUID;
import com.javachat.util.JwtUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.Ordered;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import com.javachat.service.UserDetailsServiceImpl;
import com.javachat.event_listner.IpHandshakeInterceptor;
import com.javachat.model.AnonymousUser;

@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@Configuration
@EnableWebSocketMessageBroker
public class WebsocketConfig implements WebSocketMessageBrokerConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(WebsocketConfig.class);

    @Autowired
	AuthenticationManager authenticationManager;
	@Autowired
    JwtUtils jwtUtils;
    @Autowired
    UserDetailsServiceImpl userDetailsService;
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
        .addEndpoint("/ws")
        .addInterceptors(new IpHandshakeInterceptor())
        .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                
                final StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
    
                // If new connection is detected, authenticate the connection
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    // Authenticate from the token
                    final List<String> tokenList = accessor.getNativeHeader("Authorization");
                    if (tokenList != null) {
                        String token = String.valueOf(tokenList.get(0));
                        token = jwtUtils.parseJwt(token);
                        final boolean isValidToken = jwtUtils.validateJwtToken(token);
                        // If the token is valid, set the user
                        if (isValidToken) {
                            final String email = jwtUtils.getUsernameFromToken(token);
                            final UserDetails user = userDetailsService.loadUserByUsername(email);
                            final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                user, null, user.getAuthorities());
                            accessor.setUser(authentication);
                            logger.info("User authentication for chat succeeded. Incoming email:" + email);
                        }
                    } else {
                        final AnonymousUser anonymoususer = new AnonymousUser();
                        String randomUUID = String.valueOf(UUID.randomUUID()) + "-" + String.valueOf(UUID.randomUUID());
                        anonymoususer.setUsername(randomUUID);
                        anonymoususer.setPassword(randomUUID);
                        final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                        anonymoususer, null, anonymoususer.getAuthorities());
                        accessor.setUser(authentication);
                    }
                }
                return message;
            }
        });
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/board", "/queue");   // Enables a simple in-memory broker

        //   Use this for enabling a Full featured broker 
        /*
        registry.enableStompBrokerRelay("/board")
                .setRelayHost("localhost")
                .setRelayPort(61613)
                .setClientLogin("guest")
                .setClientPasscode("guest");
        */
    }
}
