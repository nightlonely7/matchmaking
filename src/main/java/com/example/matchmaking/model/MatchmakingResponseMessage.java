package com.example.matchmaking.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MatchmakingResponseMessage {
    private SongModel selectedSong;
    private List<MatchmakingUserModel> matchedUserList;
}
