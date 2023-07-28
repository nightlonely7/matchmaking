package com.example.matchmaking.service;

import com.example.matchmaking.data.MatchmakingData;
import com.example.matchmaking.data.ModeData;
import com.example.matchmaking.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class MatchmakingService {
    private static final Random RANDOM = new Random();

    private final SimpMessageSendingOperations messagingTemplate;
    private final MatchmakingData matchmakingData; // TODO adding to repository class instead
    private final LeaderboardService leaderboardService;
    private final ModeService modeService;

    public void register(UserMessage userMessage) {
        log.info("{} attempting", userMessage.getSender());
        if (matchmakingData.getRegisteredSet().contains(userMessage.getSender())) {
            log.info("{} rejected", userMessage.getSender());
            return;
        }
        matchmakingData.getRegisteredSet().add(userMessage.getSender());
        matchmakingData.getRegisteredQueue().add(MatchmakingUserModel.builder()
                .username(userMessage.getSender())
                .host(false).build());
        log.info("registered : {}, registeredQueue ({}): {}, registeredSet ({}) {}: ",
                userMessage.getSender(),
                matchmakingData.getRegisteredQueue().size(),
                Arrays.toString(matchmakingData.getRegisteredQueue().stream().map(MatchmakingUserModel::getUsername).toArray()),
                matchmakingData.getRegisteredSet().size(),
                matchmakingData.getRegisteredSet());
        if (matchmakingData.getRegisteredQueue().size() >= matchmakingData.getRoomCount() * matchmakingData.getRoomSize()) {
            this.match();
        }
    }

    public void match() {
        log.info("=================================================================");
        log.info("start matching");
        int matchingCount = matchmakingData.getRoomCount() * matchmakingData.getRoomSize();
        int matchingTime = matchmakingData.getRegisteredQueue().size() / matchingCount;
        for (int time = 0; time < matchingTime; time++) {
            log.info("matching time : {}, registeredQueue ({}): {}, registeredSet ({}) {}: ",
                    time + 1,
                    matchmakingData.getRegisteredQueue().size(),
                    Arrays.toString(matchmakingData.getRegisteredQueue().stream().map(MatchmakingUserModel::getUsername).toArray()),
                    matchmakingData.getRegisteredSet().size(),
                    matchmakingData.getRegisteredSet());
            List<MatchmakingUserModel> matchedUserList = new ArrayList<>();
            for (int i = 0; i < matchingCount; i++) {
                matchedUserList.add(matchmakingData.getRegisteredQueue().poll());
            }
            log.info("matchedUserList : {}", matchedUserList.stream().map(MatchmakingUserModel::getUsername).toList());
            Collections.shuffle(matchedUserList);
            for (int i = 0; i < matchmakingData.getRoomCount(); i++) {
                log.info("sending chunk : {}", i + 1);
                List<MatchmakingUserModel> matchedUserChunk = matchedUserList.subList(i * matchmakingData.getRoomSize(), (i + 1) * matchmakingData.getRoomSize());
                int hostIdx = RANDOM.nextInt(matchmakingData.getRoomSize());
                matchedUserChunk.get(hostIdx).setHost(true);

                // song selection
                SongModel selectedSong = modeService.randomSongModel(modeService.getSongSelection());

                log.info("selectedSong: {}", selectedSong);

                MatchmakingResponseMessage matchmakingResponseMessage = MatchmakingResponseMessage.builder()
                        .matchedUserList(matchedUserChunk)
                        .selectedSong(selectedSong)
                        .build();

                messagingTemplate.convertAndSend("/topic/matchmaking", matchmakingResponseMessage);
                log.info("published to {} users (host : {}): {}",
                        matchmakingData.getRoomSize(),
                        matchedUserChunk.get(hostIdx).getUsername(),
                        Arrays.toString(matchedUserChunk.stream().map(MatchmakingUserModel::getUsername).toArray()));

                // joinLeaderboard
                matchedUserChunk.stream().map(MatchmakingUserModel::getUsername).toList().forEach(leaderboardService::joinUp);
                List<LeaderboardMessage> leaderboardMessageList = new ArrayList<>();
                leaderboardService.joinList().entrySet().stream()
                        .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                        .forEach(entry -> leaderboardMessageList.add(LeaderboardMessage.builder()
                                .username(entry.getKey()).score(entry.getValue()).build()));
                messagingTemplate.convertAndSend("/topic/joinLeaderboard", leaderboardMessageList);
                log.info("published joinLeaderboard : {}", leaderboardMessageList);
            }
            matchedUserList.stream().map(MatchmakingUserModel::getUsername).forEach(matchmakingData.getRegisteredSet()::remove);
        }
        log.info("end matching, registeredQueue ({}): {}, registeredSet ({}) {}: ",
                matchmakingData.getRegisteredQueue().size(),
                Arrays.toString(matchmakingData.getRegisteredQueue().stream().map(MatchmakingUserModel::getUsername).toArray()),
                matchmakingData.getRegisteredSet().size(),
                matchmakingData.getRegisteredSet());
        log.info("=================================================================");
    }

    public void remove(String username) {
        matchmakingData.getRegisteredQueue().removeIf(e -> e.getUsername().equals(username));
        matchmakingData.getRegisteredSet().removeIf(e -> e.equals(username));
        log.info("{} removed, registeredQueue ({}): {}, registeredSet ({}) {}: ",
                username,
                matchmakingData.getRegisteredQueue().size(),
                Arrays.toString(matchmakingData.getRegisteredQueue().stream().map(MatchmakingUserModel::getUsername).toArray()),
                matchmakingData.getRegisteredSet().size(),
                matchmakingData.getRegisteredSet());
    }
}
