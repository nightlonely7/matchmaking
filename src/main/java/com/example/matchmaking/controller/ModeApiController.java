package com.example.matchmaking.controller;

import com.example.matchmaking.data.ModeData;
import com.example.matchmaking.model.SongSelection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ModeApiController {

    private final ModeData modeData;

    @PostMapping("/api/songSelectionChange")
    public SongSelection changeSongSelection(@RequestBody SongSelection songSelection) {
        modeData.setSongSelection(songSelection);
        log.info("songSelection : {}", songSelection);
        return songSelection;
    }
}
