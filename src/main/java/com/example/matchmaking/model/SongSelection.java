package com.example.matchmaking.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SongSelection {
    private String mode;
    private Integer level;
}
