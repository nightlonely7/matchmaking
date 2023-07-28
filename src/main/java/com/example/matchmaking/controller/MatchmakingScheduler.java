package com.example.matchmaking.controller;

import com.example.matchmaking.service.MatchmakingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MatchmakingScheduler {
    private final MatchmakingService matchmakingService;

    @Scheduled(fixedRate = 60000)
    public void checkMatchmaking() {

    }
}
