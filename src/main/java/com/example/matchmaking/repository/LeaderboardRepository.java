package com.example.matchmaking.repository;

import com.example.matchmaking.data.LeaderboardData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class LeaderboardRepository {

    private final LeaderboardData leaderboardData;

    public void joinInit() {
        leaderboardData.setJoinLeaderboard(new LinkedHashMap<>());
    }

    public void joinUp(String username) {
        Map<String, Integer> joinLeaderboardMap = leaderboardData.getJoinLeaderboard();
        if (!joinLeaderboardMap.containsKey(username)) {
            joinLeaderboardMap.put(username, 0);
        }
        joinLeaderboardMap.put(username, joinLeaderboardMap.get(username) + 1);
    }

    public Map<String, Integer> joinList() {
        return leaderboardData.getJoinLeaderboard();
    }

    public void joinClear() {
        leaderboardData.getJoinLeaderboard().clear();
    }
}
