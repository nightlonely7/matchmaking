package com.example.matchmaking.service;

import com.example.matchmaking.data.MatchmakingData;
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
        String sender = userMessage.getSender();
        log.info("{} attempting", sender);

        if (isUserAlreadyRegistered(sender)) {
            log.info("{} rejected", sender);
            return;
        }

        registerUser(sender);
        logRegistrationDetails(sender);

        if (isReadyForMatchmaking()) {
            this.match();
        }
    }

    boolean isUserAlreadyRegistered(String sender) {
        return matchmakingData.getRegisteredSet().contains(sender);
    }

    void registerUser(String sender) {
        matchmakingData.getRegisteredSet().add(sender);
        MatchmakingUserModel user = new MatchmakingUserModel(sender, false);
        matchmakingData.getRegisteredQueue().add(user);
    }

    void logRegistrationDetails(String sender) {
        log.info("registered : {}, registeredQueue ({}): {}, registeredSet ({}) {}: ",
                sender,
                matchmakingData.getRegisteredQueue().size(),
                Arrays.toString(matchmakingData.getRegisteredQueue().stream().map(MatchmakingUserModel::getUsername).toArray()),
                matchmakingData.getRegisteredSet().size(),
                matchmakingData.getRegisteredSet());
    }

    boolean isReadyForMatchmaking() {
        return matchmakingData.getRegisteredQueue().size() >= matchmakingData.getRoomCount() * matchmakingData.getRoomSize();
    }

    public void match() {
        log.info("=================================================================");
        log.info("start matching");
        int matchingCount = calculateMatchingCount();
        int matchingTime = calculateMatchingTime(matchingCount);
        for (int time = 0; time < matchingTime; time++) {
            List<MatchmakingUserModel> matchedUserList = createMatchedUserList(matchingCount);
            processMatches(matchedUserList);
            removeMatchedUsers(matchedUserList);
        }
        log.info("end matching, registeredQueue ({}): {}, registeredSet ({}) {}: ",
                matchmakingData.getRegisteredQueue().size(),
                Arrays.toString(matchmakingData.getRegisteredQueue().stream().map(MatchmakingUserModel::getUsername).toArray()),
                matchmakingData.getRegisteredSet().size(),
                matchmakingData.getRegisteredSet());
        log.info("=================================================================");
    }

    int calculateMatchingCount() {
        return matchmakingData.getRoomCount() * matchmakingData.getRoomSize();
    }

    int calculateMatchingTime(int matchingCount) {
        if (matchingCount == 0) {
            return 0;
        }
        return matchmakingData.getRegisteredQueue().size() / matchingCount;
    }

    List<MatchmakingUserModel> createMatchedUserList(int matchingCount) {
        List<MatchmakingUserModel> matchedUserList = new ArrayList<>();
        for (int i = 0; i < matchingCount; i++) {
            matchedUserList.add(matchmakingData.getRegisteredQueue().poll());
        }
        return matchedUserList;
    }

    void processMatches(List<MatchmakingUserModel> matchedUserList) {
        Collections.shuffle(matchedUserList);
        for (int i = 0; i < matchmakingData.getRoomCount(); i++) {
            List<MatchmakingUserModel> matchedUserChunk = createMatchedUserChunk(matchedUserList, i);
            SongModel selectedSong = selectSong();
            sendMatchmakingMessage(matchedUserChunk, selectedSong);
            updateLeaderboard(matchedUserChunk);
        }
    }

    List<MatchmakingUserModel> createMatchedUserChunk(List<MatchmakingUserModel> matchedUserList, int i) {
        List<MatchmakingUserModel> matchedUserChunk = matchedUserList.subList(i * matchmakingData.getRoomSize(), (i + 1) * matchmakingData.getRoomSize());
        int hostIdx = RANDOM.nextInt(matchmakingData.getRoomSize());
        matchedUserChunk.get(hostIdx).setHost(true);
        return matchedUserChunk;
    }

    SongModel selectSong() {
        return modeService.randomSongModel(modeService.getSongSelection());
    }

    void sendMatchmakingMessage(List<MatchmakingUserModel> matchedUserChunk, SongModel selectedSong) {
        MatchmakingResponseMessage matchmakingResponseMessage = MatchmakingResponseMessage.builder()
                .matchedUserList(matchedUserChunk)
                .selectedSong(selectedSong)
                .build();
        messagingTemplate.convertAndSend("/topic/matchmaking", matchmakingResponseMessage);
    }

    void updateLeaderboard(List<MatchmakingUserModel> matchedUserChunk) {
        matchedUserChunk.stream().map(MatchmakingUserModel::getUsername).toList().forEach(leaderboardService::joinUp);
        List<LeaderboardMessage> leaderboardMessageList = new ArrayList<>();
        leaderboardService.joinList().entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .forEach(entry -> leaderboardMessageList.add(LeaderboardMessage.builder()
                        .username(entry.getKey()).score(entry.getValue()).build()));
        messagingTemplate.convertAndSend("/topic/joinLeaderboard", leaderboardMessageList);
    }

    void removeMatchedUsers(List<MatchmakingUserModel> matchedUserList) {
        matchedUserList.stream().map(MatchmakingUserModel::getUsername).forEach(matchmakingData.getRegisteredSet()::remove);
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
