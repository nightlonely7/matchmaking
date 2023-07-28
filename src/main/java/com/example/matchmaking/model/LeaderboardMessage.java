package com.example.matchmaking.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeaderboardMessage {
    private String username;
    private Integer score;
}
