package com.example.matchmaking.data;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
public class LeaderboardData {
    Map<String, Integer> joinLeaderboard;
}
