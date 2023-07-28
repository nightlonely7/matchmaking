package com.example.matchmaking.model;

import lombok.Data;

import java.util.List;

@Data
public class SongModel {
    private String mode;
    private String name;
    private String composer;
    private Integer level;
    private Integer bpm;
    private List<String> tags;
}
