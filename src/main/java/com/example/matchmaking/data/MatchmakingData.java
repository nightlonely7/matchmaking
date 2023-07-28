package com.example.matchmaking.data;

import com.example.matchmaking.model.MatchmakingUserModel;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.Set;

@Data
@Component
public class MatchmakingData {
    private Integer roomSize;
    private Integer roomCount;
    private Queue<MatchmakingUserModel> registeredQueue;
    private Set<String> registeredSet;
}
