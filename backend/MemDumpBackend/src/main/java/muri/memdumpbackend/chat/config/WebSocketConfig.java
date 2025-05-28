package muri.memdumpbackend.chat.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import muri.memdumpbackend.model.Role;
import muri.memdumpbackend.util.JWTUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Collections;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
@AllArgsConstructor
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final JWTUtil jwtUtil;
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-chat").setAllowedOriginPatterns(
                "http://localhost:[*]",
                "http://127.0.0.1:[*]"
        ).addInterceptors(new HandshakeInterceptor() {
            @Override
            public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                           WebSocketHandler wsHandler, Map<String, Object> attributes) {
                String query = request.getURI().getQuery();
                String accessToken = null;
                if (query != null && query.contains("access_token=")) {
                    try {
                        accessToken = query.split("access_token=")[1].split("&")[0];
                        log.debug("Handshake extracted access_token: <REDACTED>");
                    } catch (Exception e) {
                        log.error("Failed to parse access_token from query: {}", query, e);
                    }
                }
                if (accessToken != null && !accessToken.isBlank()) {
                    attributes.put("access_token", accessToken);
                    log.debug("Stored access_token in simpSessionAttributes");
                } else {
                    log.warn("No access_token in handshake query: {}", query);
                }
                return true; // Continue handshake
            }

            @Override
            public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                       WebSocketHandler wsHandler, Exception exception) {
                // No-op
            }
        });;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/queue");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (accessor != null && (StompCommand.CONNECT.equals(accessor.getCommand()) ||
                        StompCommand.SEND.equals(accessor.getCommand()) ||
                        StompCommand.SUBSCRIBE.equals(accessor.getCommand())
                        ) ) {
                    log.debug("Processing STOMP {} command", accessor.getCommand());

                    String token = null;
                    if (accessor.getSessionAttributes() != null) {
                        token = (String) accessor.getSessionAttributes().get("access_token");
                        log.debug("Access_token from simpSessionAttributes: {}", token != null ? "<REDACTED>" : "null");
                    }

                    if (token != null && !token.isBlank()) {
                        try {
                            if (jwtUtil.isTokenValid(token)) {
                                String username = jwtUtil.getSubject(token);
                                Role role = jwtUtil.getRole(token);
                                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                        username, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name())));
                                SecurityContextHolder.getContext().setAuthentication(auth);
                                accessor.setUser(auth);
                                log.info("Authenticated STOMP {} for user: {}", accessor.getCommand(), username);
                            } else {
                                log.warn("Invalid access_token for STOMP {} command", accessor.getCommand());
                                throw new IllegalArgumentException("Invalid access_token");
                            }
                        } catch (Exception e) {
                            log.error("Token validation failed for STOMP {}: {}", accessor.getCommand(), e.getMessage());
                            throw new IllegalArgumentException("Token validation failed: " + e.getMessage());
                        }
                    } else {
                        log.error("No access_token in simpSessionAttributes for STOMP {}", accessor.getCommand());
                        throw new IllegalArgumentException("Access token is required");
                    }
                }
                return message;
            }
        });
    }
}
