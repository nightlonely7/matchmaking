package com.example.matchmaking.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MatchmakingUserModel {
    private String username;
    private Boolean host;
}
