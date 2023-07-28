package com.example.matchmaking.service;

import com.example.matchmaking.model.CountMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CountingService {
    private static final String CONNECTED_KEY = "connected";
    private static final Map<String, Integer> COUNT_MAP = new LinkedHashMap<>();

    private final SimpMessageSendingOperations messagingTemplate;

    public void connectedUp() {
        COUNT_MAP.put(CONNECTED_KEY, COUNT_MAP.get(CONNECTED_KEY) + 1);
        log.info("COUNT_MAP - {} : {}", CONNECTED_KEY, COUNT_MAP.get(CONNECTED_KEY));
        messagingTemplate.convertAndSend("/topic/count", CountMessage.builder()
                .connected(COUNT_MAP.get(CONNECTED_KEY)).build());
    }

    public void connectedDown() {
        COUNT_MAP.put(CONNECTED_KEY, COUNT_MAP.get(CONNECTED_KEY) - 1);
        log.info("COUNT_MAP - {} : {}", CONNECTED_KEY, COUNT_MAP.get(CONNECTED_KEY));
        messagingTemplate.convertAndSend("/topic/count", CountMessage.builder()
                .connected(COUNT_MAP.get(CONNECTED_KEY)).build());
    }

    public void init() {
        COUNT_MAP.clear();
        COUNT_MAP.put(CONNECTED_KEY, 0);
        log.info("COUNT_MAP - {} : {}", CONNECTED_KEY, COUNT_MAP.get(CONNECTED_KEY));
    }
}
