package com.example.matchmaking.model;

import lombok.Data;

@Data
public class RoomChangeRequest {
    private Integer roomSize;
    private Integer roomCount;
}
