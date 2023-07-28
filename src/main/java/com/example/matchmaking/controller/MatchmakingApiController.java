package com.example.matchmaking.controller;

import com.example.matchmaking.data.MatchmakingData;
import com.example.matchmaking.model.RoomChangeRequest;
import com.example.matchmaking.service.MatchmakingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MatchmakingApiController {

    private final MatchmakingData matchmakingData;
    private final MatchmakingService matchmakingService;

    @PostMapping("/api/roomChange")
    public RoomChangeRequest changeMatchmakingData(@RequestBody RoomChangeRequest roomChangeRequest) {
        log.info("change from room size : {}, room count : {}", matchmakingData.getRoomSize(), matchmakingData.getRoomCount());
        matchmakingData.setRoomSize(roomChangeRequest.getRoomSize());
        matchmakingData.setRoomCount(roomChangeRequest.getRoomCount());
        log.info("change to room size : {}, room count : {}", matchmakingData.getRoomSize(), matchmakingData.getRoomCount());
        if (matchmakingData.getRegisteredQueue().size() >= matchmakingData.getRoomCount() * matchmakingData.getRoomSize()) {
            matchmakingService.match();
        }
        return roomChangeRequest;
    }
}
