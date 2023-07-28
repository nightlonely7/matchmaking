package com.example.matchmaking.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CountMessage {
    private Integer connected;
}
