package com.example.matchmaking.repository;

import com.example.matchmaking.data.ModeData;
import com.example.matchmaking.model.SongModel;
import com.example.matchmaking.model.SongSelection;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ModeRepository {
    private static final String BEAT_UP_MODE = "BEAT_UP";
    private static final String BEAT_RUSH_MODE = "BEAT_RUSH";
    private static final String SONG_JSON_PATH = "classpath:song.json";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final ModeData modeData;
    private final ResourceLoader resourceLoader;

    @SneakyThrows
    public void init() {
        Resource resource = resourceLoader.getResource(SONG_JSON_PATH);
        List<SongModel> songModelList = OBJECT_MAPPER.readValue(resource.getURL(), new TypeReference<>(){});
        modeData.setSongList(songModelList);
        modeData.setSongSelection(SongSelection.builder()
                .mode(BEAT_RUSH_MODE)
                .level(1).build());
    }

    public List<SongModel> listSongModel() {
        return modeData.getSongList();
    }

    public SongSelection getSongSelection() {
        return modeData.getSongSelection();
    }
}
