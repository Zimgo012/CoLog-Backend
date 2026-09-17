package com.zimgo.colog.config;

import com.zimgo.colog.auth.security.WebSocketAuthInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Component
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${app.frontend-url}")
    private String frontendUrl;

    // injects custom interceptor
    private final WebSocketAuthInterceptor webSocketAuthInterceptor;

    public WebSocketConfig(WebSocketAuthInterceptor webSocketAuthInterceptor){
        this.webSocketAuthInterceptor = webSocketAuthInterceptor;
    }
    @Override
    public void configureMessageBroker(org.springframework.messaging.simp.config.MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue", "/demo");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(org.springframework.web.socket.config.annotation.StompEndpointRegistry registry) {
//        registry.addEndpoint("/ws").withSockJS();
        registry.addEndpoint("/ws")
                .setAllowedOrigins(frontendUrl);
//                .setAllowedOriginPatterns("*");

    }


    @Override
    public void configureClientInboundChannel(ChannelRegistration registration){
        //add interceptor
        registration.interceptors(webSocketAuthInterceptor);

    }

}
