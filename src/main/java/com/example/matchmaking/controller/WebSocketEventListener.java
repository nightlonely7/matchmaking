package com.example.matchmaking.controller;

import com.example.matchmaking.model.ChatMessage;
import com.example.matchmaking.service.CountingService;
import com.example.matchmaking.service.MatchmakingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final MatchmakingService matchmakingService;
    private final SimpMessageSendingOperations messagingTemplate;
    private final CountingService countingService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        MessageHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(
                event.getMessage().getHeaders(), MessageHeaderAccessor.class);
        assert headerAccessor != null;
        StompHeaderAccessor stompHeaderAccessor = MessageHeaderAccessor.getAccessor(
                (Message<?>) Objects.requireNonNull(headerAccessor.getHeader("simpConnectMessage")),
                StompHeaderAccessor.class) ;
        assert stompHeaderAccessor != null;
        String username = Objects.requireNonNull(stompHeaderAccessor.getNativeHeader("username")).get(0);
        log.info("Received a new web socket connection : " + username);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("username");
        if(username != null) {
            log.info("User Disconnected : " + username);

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(ChatMessage.MessageType.LEAVE);
            chatMessage.setSender(username);

            messagingTemplate.convertAndSend("/topic/public", chatMessage);
            matchmakingService.remove(username);
            countingService.connectedDown();
        }
    }
}