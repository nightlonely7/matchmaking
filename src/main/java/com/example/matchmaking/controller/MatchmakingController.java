package com.example.matchmaking.controller;

import com.example.matchmaking.model.UserMessage;
import com.example.matchmaking.service.MatchmakingService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MatchmakingController {

    private final MatchmakingService matchmakingService;

    @MessageMapping("/matchmaking.register")
    public void register(@Payload UserMessage userMessage) {
        matchmakingService.register(userMessage);
    }
}
