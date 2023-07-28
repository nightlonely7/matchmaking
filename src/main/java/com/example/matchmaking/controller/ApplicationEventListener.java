package com.example.matchmaking.controller;

import com.example.matchmaking.data.MatchmakingData;
import com.example.matchmaking.model.MatchmakingUserModel;
import com.example.matchmaking.service.CountingService;
import com.example.matchmaking.service.LeaderboardService;
import com.example.matchmaking.service.ModeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.concurrent.LinkedBlockingQueue;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationEventListener {

    private static final Integer INIT_ROOM_SIZE = 4;
    private static final Integer INIT_ROOM_COUNT = 1;

    private final CountingService countingService;
    private final MatchmakingData matchmakingData;
    private final LeaderboardService leaderboardService;
    private final ModeService modeService;

    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {
        log.info("application started");
        countingService.init();
        leaderboardService.joinInit();
        matchmakingData.setRoomSize(INIT_ROOM_SIZE);
        matchmakingData.setRoomCount(INIT_ROOM_COUNT);
        log.info("room size : {}, room count : {}", matchmakingData.getRoomSize(), matchmakingData.getRoomCount());
        matchmakingData.setRegisteredQueue(new LinkedBlockingQueue<>());
        matchmakingData.setRegisteredSet(new LinkedHashSet<>());
        log.info("registeredQueue : " + Arrays.toString(matchmakingData.getRegisteredQueue().stream().map(MatchmakingUserModel::getUsername).toArray()) +
                ", registeredSet : " + matchmakingData.getRegisteredSet());
        modeService.init();
        log.info("songModelList : {}", modeService.listSongModel());
    }
}
