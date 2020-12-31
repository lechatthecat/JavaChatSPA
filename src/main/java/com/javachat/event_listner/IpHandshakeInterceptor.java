package com.javachat.event_listner;

import java.util.Map;

import com.javachat.util.HttpReqRespUtils;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

public class IpHandshakeInterceptor implements HandshakeInterceptor {

    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
            WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        String ipAddress = HttpReqRespUtils.getClientIpAddressIfServletRequestExist();
        // Set ip attribute to WebSocket session
        attributes.put("ip", ipAddress);

        return true;
    }

    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
            WebSocketHandler wsHandler, Exception exception) {          
    }
}