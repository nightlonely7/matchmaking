package com.example.matchmaking.service;

import com.example.matchmaking.repository.LeaderboardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class LeaderboardService {

    private final LeaderboardRepository leaderboardRepository;

    public void joinInit() {
        leaderboardRepository.joinInit();
    }

    public void joinUp(String username) {
        leaderboardRepository.joinUp(username);
    }

    public Map<String, Integer> joinList() {
        return leaderboardRepository.joinList();
    }

    public void joinClear() {
        leaderboardRepository.joinClear();
    }
}
